<template>
  <v-card class="mt-4 pt-2">
    <template #title>
      <v-row align-content="center">
        <v-col class="d-flex align-center justify-start">
          <div class="text-h6 pl-2">Feiertage</div>
        </v-col>
        <v-col class="d-flex align-center justify-end">
          <slot name="action" >
            <v-btn
              id="addHolidayBtn"
              :append-icon="mdiPlus"
              color="secondary"
              @click="openAddDialog"
            >Hinzufügen
            </v-btn>
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
        {{ formatDate(item.startDate) }}
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
      v-model="isAddDialogOpen"
      max-width="500"
      min-width="250"
    >
      <template #default>
        <v-card title="Feiertag hinzufügen">
          <v-card-text>
            <v-text-field
              v-model="formData.name"
              label="Name des Feiertags"
            ></v-text-field>
            <v-date-input
              v-model="formData.startDate"
              label="Datum des Feiertags"
              :prepend-icon="mdiCalendar"
            ></v-date-input>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              text="Abbrechen"
              @click="isAddDialogOpen = false"
            ></v-btn>
            <v-btn
              color="secondary"
              @click="addHoliday(formData)"
              >Speichern</v-btn
            >
          </v-card-actions>
        </v-card>
      </template>
    </v-dialog>
    <v-dialog
      v-model="isEditDialogOpen"
      max-width="500"
      min-width="250"
    >
      <template #default>
        <v-card title="Feiertag ändern">
          <v-card-text>
            <v-text-field
              v-model="formData.name"
              label="Name des Feiertags"
            ></v-text-field>
            <v-date-input
              v-model="formData.startDate"
              label="Datum des Feiertags"
              :prepend-icon="mdiCalendar"
            ></v-date-input>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              text="Abbrechen"
              @click="isEditDialogOpen = false"
            ></v-btn>
            <v-btn
              color="secondary"
              @click="editHoliday(formData)"
              >Speichern</v-btn
            >
          </v-card-actions>
        </v-card>
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

import { mdiCalendar, mdiDelete, mdiPencilOutline, mdiPlus } from "@mdi/js";
import { onMounted, ref } from "vue";

import { Levels } from "@/api/error.ts";
import {
  useAddHoliday,
  useDeleteHoliday,
  useEditHoliday,
  useGetPublicHolidays,
} from "@/composables/api/useHolidayApi.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

onMounted(async () => await loadPublicHolidays());

const isAddDialogOpen = ref(false);

const openAddDialog = () => { isAddDialogOpen.value = true;}

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

function formatDate(dateFromServer: Date | undefined): string | undefined {
  const date = dateFromServer ? new Date(dateFromServer) : undefined;
  return date
    ? date.toLocaleDateString("de-DE", {
        year: "2-digit",
        day: "2-digit",
        month: "2-digit",
      })
    : "kein Datum gefunden";
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

const addHoliday = async (
  formData: HolidayRequestDTO
) => {
  await addHolidayCall({
    holidayRequestDTO: {
      name: formData.name,
      startDate: formData.startDate,
      endDate: formData.startDate,
    },
  });
  if (!addHolidayError.value) {
    await loadPublicHolidays();
    snackbarStore.add({
      level: Levels.INFO,
      message: `Feiertag ${formData.name} am ${formData.startDate} hinzugefügt`,
    });
  } else {
    snackbarStore.add({
      level: Levels.ERROR,
      message: `Es gab einen Fehler beim Erstellen des Feiertages ${formData.name} am ${formData.startDate} !!!`,
    });
  }
  isAddDialogOpen.value = false;
  formData = { name: "", startDate: undefined, endDate: undefined, id: "" };
};

const headers = [
  { title: "Feiertag", key: "name", sortable: true },
  { title: "Datum", key: "date", sortable: true },
  { title: "Aktionen", key: "actions", align: "end", sortable: false },
] as const;
</script>

<style scoped></style>
