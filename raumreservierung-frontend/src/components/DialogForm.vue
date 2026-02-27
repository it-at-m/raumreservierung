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
const { loading, isPublic } = defineProps<{
  loading: boolean;
  isPublic: boolean;
}>();

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
      <v-card variant="outlined">
        <template #title>
          <div class="v-label font-weight-regular">
            {{ t("domain.holidays.public.date") }}
          </div>
        </template>
        <v-row class="px-2">
          <v-col
            cols="12"
            lg="6"
          >
            <v-date-input
              v-model="modelValue.startDate"
              :label="
                isPublic ? undefined : t('domain.holidays.school.startDate')
              "
              :prepend-icon="mdiCalendar"
              variant="underlined"
            ></v-date-input>
          </v-col>
          <v-col
            cols="12"
            lg="6"
            v-if="!isPublic"
          >
            <v-date-input
              v-model="modelValue.endDate"
              :label="t('domain.holidays.school.endDate')"
              :prepend-icon="mdiCalendar"
              variant="underlined"
            ></v-date-input>
          </v-col>
        </v-row>
      </v-card>
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
