<template>
  <base-view :header-text="t('generics.manage', { domain: computedTitle })">
    <template #default>
      <year-slider
        v-model="selectedYear"
        :start-year="currentYear - PREVIOUS_YEARS"
        :end-year="currentYear + NEXT_YEARS"
        class="mb-4"
      />
      <crud-card
        ref="tableRef"
        :empty-item-template="EMPTY_HOLIDAY"
        :domain="computedDomain"
        :loading="getHolidaysLoading || deleteHolidayLoading"
        @create="createHoliday"
        @delete="deleteHoliday"
        @update="updateHoliday"
      >
        <template #form="{ item, updateItem, updateValidity }">
          <holiday-form
            :isPublic="isPublic"
            :model-value="item"
            @update:model-value="updateItem"
            @is-valid="updateValidity"
            :disabled="updateHolidayLoading || createHolidayLoading"
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
            <template v-slot:[`item.actions`]="{ item }">
              <slot
                name="itemActions"
                :item="item"
                :openEdit="openEdit"
                :promptDelete="openDelete"
              >
                <v-row align-content="center">
                  <v-col
                    class="pa-0"
                    cols="12"
                    sm="6"
                  >
                    <action-button
                      type="edit"
                      class="mr-1"
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
  useGetHolidays,
  useUpdateHoliday,
} from "@/composables/api/useHolidayApi.ts";
import { DATE_FORMAT_DDMMYY } from "@/constants.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { ROUTES } from "@/types/Routes.ts";

const PREVIOUS_YEARS = 5;
const NEXT_YEARS = 10;

onMounted(async () => {
  await getHolidaysCall({ isPublic: isPublic.value });
});

const selectedYear = ref(new Date().getFullYear());
const currentYear = computed(() => new Date().getFullYear());

const filteredHolidays = computed(
  () =>
    getHolidaysData.value?.filter(
      (holiday) => holiday.startDate.getFullYear() === selectedYear.value
    ) || []
);

const { t } = useI18n();
const route = useRoute();
const snackbar = useSnackbarStore();
const tableRef = useTemplateRef("tableRef");

const {
  call: getHolidaysCall,
  data: getHolidaysData,
  loading: getHolidaysLoading,
} = useGetHolidays();

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

const isPublic = computed(() => {
  return route.name === ROUTES.PUBLIC_HOLIDAYS;
});

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

const toApiDate = (date: Date): Date => {
  return new Date(date.getTime() - date.getTimezoneOffset() * 60000);
};

const toApiHoliday = (holiday: HolidayResponseDTO): HolidayRequestDTO => {
  return {
    ...holiday,
    startDate: toApiDate(holiday.startDate),
    endDate: toApiDate(holiday.endDate),
  };
};

const createHoliday = async (holiday: Partial<HolidayResponseDTO>) => {
  await createHolidayCall({
    holidayRequestDTO: toApiHoliday(holiday as HolidayResponseDTO),
  });
  if (!createHolidayError.value) {
    await onSuccess(
      t("generics.created", {
        domain: computedDomain.value,
      })
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
      await onSuccess(
        t("generics.updated", {
          domain: computedDomain.value,
        })
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

const onSuccess = async (msg: string) => {
  await getHolidaysCall({ isPublic: isPublic.value });
  if (tableRef.value) {
    tableRef.value.closeDialog();
  }
  snackbar.add({ level: Levels.SUCCESS, message: msg });
};

const EMPTY_HOLIDAY: Partial<HolidayResponseDTO> = {
  name: "",
  id: "",
  startDate: undefined,
  endDate: undefined,
};
</script>

<style scoped></style>
