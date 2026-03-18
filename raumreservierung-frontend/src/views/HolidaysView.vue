<template>
  <generic-table-crud-view
    ref="tableRef"
    :domain="computedDomain"
    :items="getHolidaysData || []"
    :headers="headers"
    :empty-item-template="EMPTY_HOLIDAY"
    :loading="getHolidaysLoading || deleteHolidayLoading"
    @create="createHoliday"
    @delete="deleteHoliday"
    @update="updateHoliday"
  >
    <template #header>
      {{ t("generics.manage", { domain: computedTitle }) }}
    </template>
    <template #form="{ item, updateItem, updateValidity }">
      <holiday-form
        :isPublic="isPublic"
        :model-value="item"
        @update:model-value="updateItem"
        @is-valid="updateValidity"
        :disabled="updateHolidayLoading || createHolidayLoading"
      />
    </template>
    <template #[`item.startDate`]="{ item }">
      {{ useDateFormat(item.startDate, DATE_FORMAT_DDMMYY) }}
    </template>
    <template #[`item.endDate`]="{ item }">
      {{ useDateFormat(item.endDate, DATE_FORMAT_DDMMYY) }}
    </template>
  </generic-table-crud-view>
</template>

<script setup lang="ts">
import type {
  HolidayRequestDTO,
  HolidayResponseDTO,
} from "@/api/raumreservierung-backend";
import type { TableHeader } from "@/components/common/CardTable.vue";

import { useDateFormat } from "@vueuse/core";
import { computed, onMounted, useTemplateRef } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute } from "vue-router";

import { Levels } from "@/api/error.ts";
import GenericTableCrudView from "@/components/common/GenericTableCrudView.vue";
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

onMounted(async () => {
  await getHolidaysCall({ isPublic: isPublic.value });
});

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

const createHoliday = async (holiday: HolidayResponseDTO) => {
  await createHolidayCall({
    holidayRequestDTO: toApiHoliday(holiday),
  });
  if (!createHolidayError.value) {
    await onSuccess(
      t("generics.created", {
        domain: computedDomain.value,
      })
    );
  }
};

const updateHoliday = async (holiday: HolidayResponseDTO) => {
  if (holiday.id) {
    await updateHolidayCall({
      id: holiday.id,
      holidayRequestDTO: toApiHoliday(holiday),
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

type HolidayForm = Partial<HolidayResponseDTO>;

const EMPTY_HOLIDAY: HolidayForm = {
  name: "",
  id: "",
  startDate: undefined,
  endDate: undefined,
};
</script>

<style scoped></style>
