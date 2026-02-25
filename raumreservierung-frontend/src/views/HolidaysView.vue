<template>
  <generic-table-crud-view
    ref="tableRef"
    :domain="t('domain.holidays.public.header', { count: 2 })"
    :items="getPublicHolidaysData ?? []"
    :headers="headers"
    :empty-item-template="EMPTY_HOLIDAY"
    :loading="getPublicHolidaysLoading || addHolidayLoading"
    @create="createHoliday"
    @delete="deleteHoliday"
    @update="updateHoliday"
  >
    <template #form="{ item, updateItem, save, cancel }">
      <dialog-form
        :model-value="item"
        @update:model-value="updateItem"
        @close="cancel"
        @addHoliday="save"
        :loading="
          deleteHolidayLoading || editHolidayLoading || addHolidayLoading
        "
      />
    </template>
    <template #[`item.date`]="{ item }">
      {{ useDateFormat(item.startDate, "DD.MM.YY") }}
    </template>
  </generic-table-crud-view>
</template>

<script setup lang="ts">
import type { HolidayResponseDTO } from "@/api/raumreservierung-backend";
import type { TableHeader } from "@/components/common/CardTable.vue";
import type { Ref } from "vue";

import { useDateFormat } from "@vueuse/core";
import { onMounted, useTemplateRef } from "vue";
import { useI18n } from "vue-i18n";

import { Levels } from "@/api/error.ts";
import GenericTableCrudView from "@/components/common/GenericTableCrudView.vue";
import DialogForm from "@/components/DialogForm.vue";
import {
  useAddHoliday,
  useDeleteHoliday,
  useEditHoliday,
  useGetPublicHolidays,
} from "@/composables/api/useHolidayApi.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

onMounted(async () => await getPublicHolidaysCall({ isPublic: true }));

const { t } = useI18n();

const snackbar = useSnackbarStore();
const tableRef = useTemplateRef("tableRef");
const {
  call: getPublicHolidaysCall,
  data: getPublicHolidaysData,
  loading: getPublicHolidaysLoading,
} = useGetPublicHolidays();

const {
  call: addHolidayCall,
  loading: addHolidayLoading,
  error: addHolidayError,
} = useAddHoliday();

const {
  call: editHolidayCall,
  loading: editHolidayLoading,
  error: editHolidayError,
} = useEditHoliday();

const {
  call: deleteHolidayCall,
  loading: deleteHolidayLoading,
  error: deleteHolidayError,
} = useDeleteHoliday();

const createHoliday = async (holiday: HolidayResponseDTO) => {
  await addHolidayCall({
    holidayRequestDTO: { ...holiday, endDate: holiday.startDate },
  });
  await fetchAndClose(
    addHolidayError,
    t("generics.snackbar.created", {
      domain: t("domain.holidays.public.header", { count: 1 }),
    })
  );
};

const updateHoliday = async (holiday: HolidayResponseDTO) => {
  if (holiday.id) {
    await editHolidayCall({
      id: holiday.id,
      holidayRequestDTO: {
        ...holiday,
        startDate: toDate(holiday.startDate),
        endDate: toDate(holiday.startDate),
      },
    });
    await fetchAndClose(
      editHolidayError,
      t("generics.snackbar.edited", {
        domain: t("domain.holidays.public.header", { count: 1 }),
      })
    );
  }
};

const toDate = (date: Date | undefined) => {
  return date ? new Date(date) : undefined;
};

const deleteHoliday = async (id: string) => {
  await deleteHolidayCall({ id: id });
  await fetchAndClose(
    deleteHolidayError,
    t("generics.snackbar.deleted", {
      domain: t("domain.holidays.public.header", { count: 1 }),
    })
  );
};

const fetchAndClose = async (errorRef: Ref<boolean>, msg: string) => {
  if (tableRef.value) {
    tableRef.value.closeDialog();
  }
  if (!errorRef.value) {
    snackbar.add({ level: Levels.SUCCESS, message: msg });
  }
  await getPublicHolidaysCall({ isPublic: true });
};

const headers: TableHeader<HolidayResponseDTO>[] = [
  {
    title: t("domain.holidays.public.header", { count: 1 }),
    value: "name",
    sortable: true,
  },
  { title: t("domain.holidays.public.date"), value: "date", sortable: false },
  {
    title: t("common.action", { count: 2 }),
    value: "actions",
    align: "end",
    sortable: false,
  },
];

const EMPTY_HOLIDAY: HolidayResponseDTO = {
  name: "",
  id: "",
  startDate: undefined,
  endDate: undefined,
};
</script>

<style scoped></style>
