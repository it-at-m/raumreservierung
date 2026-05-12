<template>
  <base-view :header-text="t('generics.manage', { domain: computedTitle })">
    <template #default>
      <year-slider
        v-model="selectedYear"
        :end-year="currentYear + NEXT_YEARS"
        :start-year="currentYear - PREVIOUS_YEARS"
        class="mb-4"
        @update:model-value="updatedYearSelection"
      />
      <crud-card
        ref="tableRef"
        :domain="computedDomain"
        :empty-item-template="EMPTY_HOLIDAY"
        :loading="holidayStore.loading || deleteHolidayLoading"
        @create="createHoliday"
        @delete="deleteHoliday"
        @update="updateHoliday"
      >
        <template #form="{ item, updateItem, updateValidity }">
          <holiday-form
            :disabled="updateHolidayLoading || createHolidayLoading"
            :is-public="isPublic"
            :model-value="item"
            @update:model-value="updateItem"
            @is-valid="updateValidity"
          />
        </template>
        <template #table="{ openEdit, openDelete }">
          <v-data-table
            :headers="headers"
            :items="filteredHolidays || []"
            hide-default-footer
            items-per-page="-1"
          >
            <template #[`item.startDate`]="{ item }">
              {{ useDateFormat(item.startDate, DATE_FORMAT_DDMMYY) }}
            </template>
            <template #[`item.endDate`]="{ item }">
              {{ useDateFormat(item.endDate, DATE_FORMAT_DDMMYY) }}
            </template>
            <template #[`item.actions`]="{ item }">
              <slot
                :item="item"
                :open-edit="openEdit"
                :prompt-delete="openDelete"
                name="itemActions"
              >
                <v-row align-content="center">
                  <v-col
                    class="pa-0"
                    cols="12"
                    sm="6"
                  >
                    <action-button
                      class="mr-1"
                      type="edit"
                      @click="openEdit(item)"
                    />
                  </v-col>
                  <v-col
                    class="pa-0"
                    cols="12"
                    sm="6"
                  >
                    <action-button
                      type="delete"
                      @click="openDelete(item)"
                    />
                  </v-col>
                </v-row>
              </slot>
            </template>
          </v-data-table>
        </template>
      </crud-card>
    </template>
  </base-view>
</template>

<script setup lang="ts">
import type {
  HolidayRequestDTO,
  HolidayResponseDTO,
} from "@/api/raumreservierung-backend";
import type { TableHeader } from "@/types/TableHeader.ts";

import { useDateFormat } from "@vueuse/core";
import { computed, onMounted, ref, useTemplateRef } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute } from "vue-router";

import { Levels } from "@/api/error.ts";
import BaseView from "@/components/common/BaseView.vue";
import ActionButton from "@/components/common/buttons/ActionButton.vue";
import CrudCard from "@/components/common/CrudCard.vue";
import YearSlider from "@/components/common/YearSlider.vue";
import HolidayForm from "@/components/HolidayForm.vue";
import {
  useCreateHoliday,
  useDeleteHoliday,
  useUpdateHoliday,
} from "@/composables/api/useHolidayApi.ts";
import { DATE_FORMAT_DDMMYY } from "@/constants.ts";
import { useHolidayStore } from "@/stores/holiday.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { ROUTES } from "@/types/Routes.ts";

const PREVIOUS_YEARS = 5;
const NEXT_YEARS = 10;

const selectedYear = ref(new Date().getFullYear());
const currentYear = computed(() => new Date().getFullYear());

onMounted(async () => {
  await holidayStore.loadYear(currentYear.value);
});

const { t } = useI18n();
const route = useRoute();
const snackbar = useSnackbarStore();
const tableRef = useTemplateRef("tableRef");

const holidayStore = useHolidayStore();

const isPublic = computed(() => {
  return route.name === ROUTES.PUBLIC_HOLIDAYS;
});

const filteredHolidays = computed(() => {
  return holidayStore.currentHolidays.filter(
    (holiday) =>
      (holiday.startDate.getTime() === holiday.endDate.getTime()) ===
      isPublic.value
  );
});

const {
  call: createHolidayCall,
  loading: createHolidayLoading,
  error: createHolidayError,
} = useCreateHoliday();

const {
  call: updateHolidayCall,
  loading: updateHolidayLoading,
  error: updateHolidayError,
} = useUpdateHoliday();

const {
  call: deleteHolidayCall,
  loading: deleteHolidayLoading,
  error: deleteHolidayError,
} = useDeleteHoliday();

const computedTitle = computed(() =>
  isPublic.value
    ? t("domain.holidays.public.header", { count: 2 })
    : t("domain.holidays.school.header")
);

const computedDomain = computed(() =>
  isPublic.value
    ? t("domain.holidays.public.header")
    : t("domain.holidays.school.header")
);

const toApiDate = (date: Date): Date => {
  const d = new Date(date);
  return new Date(d.getTime() - d.getTimezoneOffset() * 60000);
};

const toApiHoliday = (holiday: HolidayResponseDTO): HolidayRequestDTO => {
  return {
    ...holiday,
    startDate: toApiDate(holiday.startDate),
    endDate: toApiDate(holiday.endDate),
  };
};

const updatedYearSelection = (value: number) => {
  selectedYear.value = value;
  holidayStore.loadYear(selectedYear.value);
};

const createHoliday = async (holiday: Partial<HolidayResponseDTO>) => {
  await createHolidayCall({
    holidayRequestDTO: toApiHoliday(holiday as HolidayResponseDTO),
  });
  if (!createHolidayError.value) {
    await onSuccess(
      t("generics.created", {
        domain: computedDomain.value,
      }),
      holiday.startDate?.getFullYear()
    );
  }
};

const updateHoliday = async (holiday: Partial<HolidayResponseDTO>) => {
  if (holiday.id) {
    await updateHolidayCall({
      id: holiday.id,
      holidayRequestDTO: toApiHoliday(holiday as HolidayResponseDTO),
    });
    if (!updateHolidayError.value) {
      const startDate = holiday.startDate ? new Date(holiday.startDate) : null;
      const year = startDate?.getFullYear();
      await onSuccess(
        t("generics.updated", {
          domain: computedDomain.value,
        }),
        year
      );
    }
  }
};

const deleteHoliday = async (id: string) => {
  await deleteHolidayCall({ id: id });
  if (!deleteHolidayError.value) {
    await onSuccess(
      t("generics.deleted", {
        domain: t("domain.holidays.public.header"),
      })
    );
  }
};

const onSuccess = async (msg: string, yearOverride?: number) => {
  await holidayStore.loadYear(yearOverride || selectedYear.value, true);
  if (tableRef.value) {
    tableRef.value.closeDialog();
  }
  snackbar.add({ level: Levels.SUCCESS, message: msg });
};

const headers = computed((): TableHeader<HolidayResponseDTO>[] => {
  return [
    { title: computedDomain.value, value: "name", sortable: true },
    {
      title: isPublic.value
        ? t("domain.holidays.public.date")
        : t("domain.holidays.school.startDate"),
      value: "startDate",
      sortable: true,
    },
    ...(isPublic.value
      ? []
      : [{ title: t("domain.holidays.school.endDate"), value: "endDate" }]),
    { title: t("common.action", { count: 2 }), value: "actions", width: "12%" },
  ];
});

const EMPTY_HOLIDAY: Partial<HolidayResponseDTO> = {
  name: "",
  id: "",
  startDate: undefined,
  endDate: undefined,
};
</script>

<style scoped></style>
