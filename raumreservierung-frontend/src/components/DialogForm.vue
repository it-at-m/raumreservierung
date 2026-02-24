<script setup lang="ts">
import type { HolidayRequestDTO } from "@/api/raumreservierung-backend";

import { mdiCalendar } from "@mdi/js";

import BaseButton from "@/components/common/BaseButton.vue";

const emit = defineEmits<{
  addHoliday: [item: HolidayRequestDTO];
  close: [];
}>();
const modelValue = defineModel<HolidayRequestDTO>({ required: true });
</script>

<template>
  <v-card title="Feiertag hinzufügen">
    <v-card-text>
      <v-text-field
        v-model="modelValue.name"
        label="Name des Feiertags"
        variant="outlined"
      ></v-text-field>
      <v-date-input
        v-model="modelValue.startDate"
        label="Datum des Feiertags"
        :prepend-icon="mdiCalendar"
        variant="outlined"
      ></v-date-input>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <base-button
        @click="emit('close')"
        secondary
      >
        Abbrechen
      </base-button>
      <base-button @click="emit('addHoliday', modelValue)">
        Speichern
      </base-button>
    </v-card-actions>
  </v-card>
</template>

<style scoped></style>
