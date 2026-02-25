<script setup lang="ts">
import type { HolidayRequestDTO } from "@/api/raumreservierung-backend";

import { mdiCalendar } from "@mdi/js";
import { useI18n } from "vue-i18n";

import BaseButton from "@/components/common/BaseButton.vue";

const emit = defineEmits<{
  addHoliday: [item: HolidayRequestDTO];
  close: [];
}>();
const modelValue = defineModel<HolidayRequestDTO>({ required: true });
const { loading } = defineProps<{ loading: boolean }>();

const { t } = useI18n();
</script>

<template>
  <v-card
    :title="
      t('generics.create', {
        domain: t('domain.holidays.public.header', { count: 1 }),
      })
    "
    :loading="loading"
  >
    <v-card-text>
      <v-text-field
        v-model="modelValue.name"
        :label="t('domain.holidays.public.name')"
        variant="outlined"
      ></v-text-field>
      <v-date-input
        v-model="modelValue.startDate"
        :label="t('domain.holidays.public.date')"
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
