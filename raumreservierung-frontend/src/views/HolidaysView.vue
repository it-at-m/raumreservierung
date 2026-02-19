<template>
  <v-container>
    <v-card
      variant="outlined"
      class="ma-4"
    >
      <v-card-title class="d-flex align-center justify-space-between py-4">
        <span>Feiertage</span>
        <v-btn
          id="addHolidayBtn"
          :append-icon="mdiPlus"
          color="secondary"
          >Hinzufügen
        </v-btn>
      </v-card-title>
      <v-divider />
      <v-data-table
        :headers="headers"
        :items="publicHolidays"
        :loading="getPublicHolidaysLoading"
        loading-text="Lade Daten..."
        no-data-text="Keine Feiertage gefunden"
        hide-default-footer
      >
        <template #[`item.date`]="{ item }">
          {{ formatDate(item.startDate) }}
        </template>
        <template #[`item.actions`]="{ item }">
          <div class="d-flex">
            <v-btn
              :icon="mdiPencilOutline"
              variant="text"
              color="primary"
              density="compact"
              class="me-1"
              @click="editHoliday(item)"
            ></v-btn>
            <v-btn
              :icon="mdiDelete"
              variant="text"
              color="black"
              density="compact"
              @click="deleteHoliday(item)"
            ></v-btn>
          </div>
        </template>
      </v-data-table>
      <v-dialog
        activator="#addHolidayBtn"
        width="auto"
      >
        <template #default="{ isActive }">
          <v-card title="Feiertag hinzufügen">
            <v-card-text>
              <v-text-field
                v-model="formData.name"
                label="Name des Feiertags"
              ></v-text-field>
              <v-date-input
                v-model="formData.date"
                label="Datum des Feiertags"
                prepend-icon="mdi-calendar"
              ></v-date-input>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                text="Abbrechen"
                @click="isActive.value = false"
              ></v-btn>
              <v-btn
                color="secondary"
                @click="addHoliday(formData, isActive)"
                >Speichern</v-btn
              >
            </v-card-actions>
          </v-card>
        </template>
      </v-dialog>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import type { HolidayResponseDTO } from "@/api/raumreservierung-backend";
import type { Ref } from "vue";

import { mdiDelete, mdiPencilOutline, mdiPlus } from "@mdi/js";
import { onMounted, ref } from "vue";

import { Levels } from "@/api/error.ts";
import {
  useAddHoliday,
  useDeleteHoliday,
  useGetPublicHolidays,
} from "@/composables/api/useHolidayApi.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

onMounted(async () => await loadPublicHolidays());

const formData = ref({
  name: "",
  date: new Date(),
});

const snackbarStore = useSnackbarStore();

const publicHolidays: Ref<HolidayResponseDTO[]> = ref([]);

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
  }
  publicHolidays.value = JSON.parse(
    JSON.stringify(getPublicHolidaysData.value)
  );
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

const editHoliday = (holiday: HolidayResponseDTO) => {
  snackbarStore.add({
    level: Levels.INFO,
    message: `Bearbeite den Feiertag: ${holiday.name}`,
  });
};

const { call: deleteHolidayCall, error: deleteHolidayError } =
  useDeleteHoliday();

const deleteHoliday = async (holiday: HolidayResponseDTO) => {
  if (holiday.id) {
    await deleteHolidayCall({ id: holiday.id });
  }
  if (!deleteHolidayError.value) {
    await loadPublicHolidays();
    snackbarStore.add({
      level: Levels.SUCCESS,
      message: `Feiertag ${holiday.name} wurde gelöscht.`,
    });
  }
};

const {
  call: addHolidayCall,
  error: addHolidayError,
} = useAddHoliday();

const addHoliday = async (
  formData: { name: string; date: Date },
  isActive: Ref<boolean>
) => {
  await addHolidayCall({
    holidayRequestDTO: {
      name: formData.name,
      startDate: formData.date,
      endDate: formData.date,
    },
  });
  if (!addHolidayError.value) {
    await loadPublicHolidays();
    snackbarStore.add({
      level: Levels.INFO,
      message: `Feiertag ${formData.name} am ${formData.date} hinzugefügt`,
    });
  } else {
    snackbarStore.add({
      level: Levels.ERROR,
      message: `Es gab einen Fehler beim Erstellen des Feiertages ${formData.name} am ${formData.date} !!!`,
    });
  }
  isActive.value = false;
};

const headers = [
  { title: "Feiertag", key: "name", sortable: true },
  { title: "Datum", key: "date", sortable: true },
  { title: "Aktionen", key: "actions", sortable: false },
] as const;
</script>

<style scoped></style>
