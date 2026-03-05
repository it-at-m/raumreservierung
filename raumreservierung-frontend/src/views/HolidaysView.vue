<template>
  <generic-table-crud-view
    ref="tableRef"
    :domain="computedDomain"
    :items="getHolidaysData ?? []"
    :headers="headers"
    :empty-item-template="EMPTY_HOLIDAY"
    :loading="getHolidaysLoading || deleteHolidayLoading"
    @create="createHoliday"
    @delete="deleteHoliday"
    @update="updateHoliday"
  >
    <template #title>
      {{
        isPublic
          ? t("domain.holidays.public.header", { count: 2 })
          : t("domain.holidays.school.header")
      }}
    </template>
    <template #form="{ item, updateItem, updateValidity }">
      <dialog-form
        :isPublic="isPublic"
        :model-value="item"
        @update:model-value="updateItem"
        @is-valid="updateValidity"
        :disabled="editHolidayLoading || addHolidayLoading"
      />
    </template>
    <template #[`item.date`]="{ item }">
      {{ useDateFormat(item.startDate, "DD.MM.YY") }}
    </template>
    <template #[`item.startDate`]="{ item }">
      {{ useDateFormat(item.startDate, "DD.MM.YY") }}
    </template>
    <template #[`item.endDate`]="{ item }">
      {{ useDateFormat(item.endDate, "DD.MM.YY") }}
    </template>
  </generic-table-crud-view>
</template>

<script setup lang="ts">
import type { HolidayResponseDTO } from "@/api/raumreservierung-backend";
import type { TableHeader } from "@/components/common/CardTable.vue";
import type { Ref } from "vue";

import { useDateFormat } from "@vueuse/core";
import { computed, onMounted, useTemplateRef } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute } from "vue-router";

import { Levels } from "@/api/error.ts";
import GenericTableCrudView from "@/components/common/GenericTableCrudView.vue";
import DialogForm from "@/components/DialogForm.vue";
import {
  useAddHoliday,
  useDeleteHoliday,
  useEditHoliday,
  useGetHolidays,
} from "@/composables/api/useHolidayApi.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { ROUTES } from "@/types/Routes.ts";

onMounted(async () => {
  await getHolidaysCall({ isPublic: isPublic.value });
});

const route = useRoute();

const isPublic = computed(() => {
  return route.name === ROUTES.PUBLICHOLIDAYS;
});

const computedDomain = computed(() =>
  isPublic.value
    ? t("domain.holidays.public.header", { count: 1 })
    : t("domain.holidays.school.header")
);

const { t } = useI18n();

const snackbar = useSnackbarStore();
const tableRef = useTemplateRef("tableRef");

const {
  call: getHolidaysCall,
  data: getHolidaysData,
  loading: getHolidaysLoading,
} = useGetHolidays();

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
  await addHolidayCall({ holidayRequestDTO: holiday });
  await fetchAndClose(
    addHolidayError,
    t("generics.created", {
      domain: computedDomain.value,
    })
  );
};

const updateHoliday = async (holiday: HolidayResponseDTO) => {
  if (holiday.id) {
    await editHolidayCall({
      id: holiday.id,
      holidayRequestDTO: holiday,
    });
    await fetchAndClose(
      editHolidayError,
      t("generics.updated", {
        domain: computedDomain.value,
      })
    );
  }
};

const deleteHoliday = async (id: string) => {
  await deleteHolidayCall({ id: id });
  await fetchAndClose(
    deleteHolidayError,
    t("generics.deleted", {
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
  await getHolidaysCall({ isPublic: isPublic.value });
};

const headers: TableHeader<HolidayResponseDTO>[] = [
  {
    title: computedDomain.value,
    value: "name",
    sortable: true,
  },
  { title: t("domain.holidays.public.date"), value: "date", sortable: false },
  {
    title: t("common.action", { count: 2 }),
    value: "actions",
    sortable: false,
    width: "12%",
  },
];
const headerSchoolStart = {
  title: t("domain.holidays.school.startDate"),
  value: "startDate",
  sortable: false,
};
const headerSchoolEnd = {
  title: t("domain.holidays.school.endDate"),
  value: "endDate",
  sortable: false,
};

if (!isPublic.value) {
  headers.splice(1, 1, headerSchoolStart);
  headers.splice(2, 0, headerSchoolEnd);
}

const EMPTY_HOLIDAY: HolidayResponseDTO = {
  name: "",
  id: "",
  startDate: undefined,
  endDate: undefined,
};
</script>

<style scoped></style>
