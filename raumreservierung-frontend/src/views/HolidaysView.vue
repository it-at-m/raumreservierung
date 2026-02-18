<template>
  <v-container>
    <v-card
      variant="outlined"
      class="ma-4"
    >
      <v-card-title class="d-flex align-center justify-space-between py-4">
        <span>Feiertage</span>
        <v-btn
          :append-icon="mdiPlus"
          color="secondary"
          @click="addHoliday"
          >Hinzufügen</v-btn
        >
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
        <template v-slot:[`item.date`]="{ value }">
          {{ formatDate(value) }}
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
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import type { HolidayResponseDTO } from "@/api/raumreservierung-backend";
import type { Ref } from "vue";

import { mdiDelete, mdiPencilOutline, mdiPlus } from "@mdi/js";
import { onMounted, ref } from "vue";

import { Levels } from "@/api/error.ts";
import { useGetPublicHolidays } from "@/composables/api/useHolidayApi.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

onMounted(async () => await loadPublicHolidays());

const {
  call: getPublicHolidaysCall,
  data: getPublicHolidaysData,
  error: getPublicHolidaysError,
  loading: getPublicHolidaysLoading,
} = useGetPublicHolidays();

const snackbarStore = useSnackbarStore();

const publicHolidays: Ref<HolidayResponseDTO[]> = ref([]);
/**
 * Loads public holidays from the backend and sets it in the public holiday store.
 */
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
  return date?.toLocaleDateString("de-DE", {
    year: "2-digit",
    day: "2-digit",
    month: "2-digit",
  });
}

const editHoliday = (holiday: HolidayResponseDTO) => {
  snackbarStore.add({
    level: Levels.INFO,
    message: `Bearbeite den Feiertag: ${holiday.name}`,
  });
};

const deleteHoliday = (holiday: HolidayResponseDTO) => {
  snackbarStore.add({
    level: Levels.INFO,
    message: `Lösche den Feiertag: ${holiday.name}`,
  });
};

const addHoliday = () => {
  snackbarStore.add({
    level: Levels.INFO,
    message: `Füge einen Feiertag hinzu`,
  });
};

const headers = [
  { title: "Feiertag", key: "name", sortable: true },
  { title: "Datum", key: "date", sortable: true },
  { title: "Aktionen", key: "actions", sortable: false },
] as const;
</script>

<style scoped></style>
