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
      <dialog-form
        :isPublic="isPublic"
        :model-value="item"
        @update:model-value="updateItem"
        @is-valid="updateValidity"
        :disabled="editHolidayLoading || addHolidayLoading"
      />
    </template>
    <template #[`item.date`]="{ item }">
      {{ useFormatDate(item.startDate) }}
    </template>
    <template #[`item.startDate`]="{ item }">
      {{ useFormatDate(item.startDate) }}
    </template>
    <template #[`item.endDate`]="{ item }">
      {{ useFormatDate(item.endDate) }}
    </template>
  </generic-table-crud-view>
</template>

<script setup lang="ts">
import type { HolidayResponseDTO } from "@/api/raumreservierung-backend";
import type { TableHeader } from "@/components/common/CardTable.vue";

import { computed, onMounted, useTemplateRef, watch } from "vue";
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
import { useFormatDate } from "@/composables/useFormatDate.ts";
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

    ...(isPublic.value
      ? [
          {
            title: t("domain.holidays.public.date"),
            value: "date",
            sortable: true,
          },
        ]
      : [
          {
            title: t("domain.holidays.school.startDate"),
            value: "startDate",
            sortable: true,
          },
          { title: t("domain.holidays.school.endDate"), value: "endDate" },
        ]),

    { title: t("common.action", { count: 2 }), value: "actions", width: "12%" },
  ];
});

watch(isPublic, () => getHolidaysCall({ isPublic: isPublic.value }));

const createHoliday = async (holiday: HolidayResponseDTO) => {
  await addHolidayCall({ holidayRequestDTO: holiday });
  if (!addHolidayError.value) {
    await fetchAndClose(
      t("generics.created", {
        domain: computedDomain.value,
      })
    );
  }
};

const updateHoliday = async (holiday: HolidayResponseDTO) => {
  if (holiday.id) {
    await editHolidayCall({
      id: holiday.id,
      holidayRequestDTO: holiday,
    });
    if (!editHolidayError.value) {
      await fetchAndClose(
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
    await fetchAndClose(
      t("generics.deleted", {
        domain: t("domain.holidays.public.header"),
      })
    );
  }
};

const fetchAndClose = async (msg: string) => {
  await getHolidaysCall({ isPublic: isPublic.value });
  if (tableRef.value) {
    tableRef.value.closeDialog();
  }
  snackbar.add({ level: Levels.SUCCESS, message: msg });
};

const EMPTY_HOLIDAY: HolidayResponseDTO = {
  name: "",
  id: "",
  startDate: undefined,
  endDate: undefined,
};
</script>

<style scoped></style>
