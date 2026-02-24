<template>
  <v-card class="mt-4 pt-2">
    <template #title>
      <v-row align-content="center">
        <v-col class="d-flex align-center justify-start">
          <div class="text-h6 pl-2">Feiertage</div>
        </v-col>
        <v-col class="d-flex align-center justify-end">
          <slot name="action">
            <base-button @click="openAddDialog">
              <template #append> <v-icon :icon="mdiPlus" /></template>
              Hinzufügen
            </base-button>
          </slot>
        </v-col>
      </v-row>
      <v-row class="mt-2">
        <v-col>
          <v-divider />
        </v-col>
      </v-row>
    </template>
    <v-divider />
    <v-data-table
      :headers="headers"
      :items="holidayResponseDTOs"
      :loading="getPublicHolidaysLoading"
      loading-text="Lade Daten..."
      no-data-text="Keine Feiertage gefunden"
      hide-default-footer
    >
      <template #[`item.date`]="{ item }">
        {{ useDateFormat(item.startDate, "DD.MM.YY") }}
      </template>
      <template #[`item.actions`]="{ item }">
        <div>
          <v-btn
            :icon="mdiPencilOutline"
            variant="text"
            color="primary"
            density="compact"
            class="me-1"
            @click="openEditDialog(item)"
          ></v-btn>
          <v-btn
            :icon="mdiDelete"
            variant="text"
            color="black"
            density="compact"
            @click="deleteHoliday(item.id)"
          ></v-btn>
        </div>
      </template>
    </v-data-table>
    <v-dialog
      :model-value="isAddDialogOpen"
      width="90%"
      max-width="800px"
    >
      <template #default>
        <dialog-form
          v-model="formData"
          @addHoliday="addHoliday"
          @close="isAddDialogOpen = false"
        />
      </template>
    </v-dialog>
    <v-dialog
      :model-value="isEditDialogOpen"
      width="90%"
      max-width="800px"
    >
      <template #default>
        <dialog-form
          v-model="formData"
          @addHoliday="editHoliday"
          @close="isEditDialogOpen = false"
        />
      </template>
    </v-dialog>
  </v-card>
</template>

<script setup lang="ts">
import type {
  HolidayRequestDTO,
  HolidayResponseDTO,
} from "@/api/raumreservierung-backend";
import type { Ref } from "vue";

import { mdiDelete, mdiPencilOutline, mdiPlus } from "@mdi/js";
import { useDateFormat } from "@vueuse/core";
import { onMounted, ref } from "vue";

import { Levels } from "@/api/error.ts";
import BaseButton from "@/components/common/BaseButton.vue";
import DialogForm from "@/components/DialogForm.vue";
import {
  useAddHoliday,
  useDeleteHoliday,
  useEditHoliday,
  useGetPublicHolidays,
} from "@/composables/api/useHolidayApi.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

onMounted(async () => await loadPublicHolidays());

const isAddDialogOpen = ref(false);

const openAddDialog = () => {
  isAddDialogOpen.value = true;
};

const isEditDialogOpen = ref(false);

const openEditDialog = (item: HolidayRequestDTO) => {
  formData.value.id = item.id;
  formData.value.name = item.name;
  formData.value.startDate = item.startDate;
  formData.value.endDate = item.endDate;
  isEditDialogOpen.value = true;
};

const formData: Ref<HolidayRequestDTO> = ref({});

const snackbarStore = useSnackbarStore();

const holidayResponseDTOs: Ref<HolidayResponseDTO[]> = ref([]);

const {
  call: getPublicHolidaysCall,
  data: getPublicHolidaysData,
  error: getPublicHolidaysError,
  loading: getPublicHolidaysLoading,
} = useGetPublicHolidays();

async function loadPublicHolidays() {
  await getPublicHolidaysCall();
  if (getPublicHolidaysError.value) {
    snackbarStore.add({
      level: Levels.WARNING,
      message: "There was an error loading the public holidays",
    });
  } else {
    holidayResponseDTOs.value = JSON.parse(
      JSON.stringify(getPublicHolidaysData.value)
    );
  }
}

const { call: editHolidayCall, error: editHolidayError } = useEditHoliday();

const editHoliday = async (holiday: HolidayRequestDTO) => {
  await editHolidayCall({
    holidayRequestDTO: {
      ...holiday,
      startDate: holiday.startDate ? new Date(holiday.startDate) : undefined,
      endDate: holiday.startDate ? new Date(holiday.startDate) : undefined,
    },
  });
  if (!editHolidayError.value) {
    snackbarStore.add({
      level: Levels.SUCCESS,
      message: `Feiertag: ${holiday.name} bearbeitet.`,
    });
  } else {
    snackbarStore.add({
      level: Levels.ERROR,
      message: `Feiertag: ${holiday.name} konnte nicht bearbeitet werden.`,
    });
  }
  await loadPublicHolidays();
  isEditDialogOpen.value = false;
  formData.value = {
    name: "",
    startDate: undefined,
    endDate: undefined,
    id: "",
  };
};

const { call: deleteHolidayCall, error: deleteHolidayError } =
  useDeleteHoliday();

const deleteHoliday = async (holidayId: string | undefined) => {
  if (holidayId) {
    await deleteHolidayCall({ id: holidayId });
  }
  if (!deleteHolidayError.value) {
    await loadPublicHolidays();
    snackbarStore.add({
      level: Levels.SUCCESS,
      message: `Feiertag wurde gelöscht.`,
    });
  }
};

const { call: addHolidayCall, error: addHolidayError } = useAddHoliday();

const addHoliday = async (holiday: HolidayRequestDTO) => {
  await addHolidayCall({
    holidayRequestDTO: {
      name: holiday.name,
      startDate: holiday.startDate,
      endDate: holiday.startDate,
    },
  });
  if (!addHolidayError.value) {
    await loadPublicHolidays();
    snackbarStore.add({
      level: Levels.INFO,
      message: `Feiertag ${holiday.name} am ${holiday.startDate} hinzugefügt`,
    });
  } else {
    snackbarStore.add({
      level: Levels.ERROR,
      message: `Es gab einen Fehler beim Erstellen des Feiertages ${holiday.name} am ${holiday.startDate} !!!`,
    });
  }
  isAddDialogOpen.value = false;
};

const headers = [
  { title: "Feiertag", key: "name", sortable: true },
  { title: "Datum", key: "date", sortable: true },
  { title: "Aktionen", key: "actions", align: "end", sortable: false },
] as const;
</script>

<style scoped></style>
