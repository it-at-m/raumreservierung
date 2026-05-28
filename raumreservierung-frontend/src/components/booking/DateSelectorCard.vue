<template>
  <card-form subtitle="Datum und Uhrzeit">
    <template #text>
      <v-row>
        <v-checkbox
          v-model="multiDay"
          color="accent"
          label="Mehrtägiger Termin"
          hide-details
          density="compact"
        />
      </v-row>
      <v-row>
        <v-col>
          <date-time-text-field
            v-model="occupancyStart"
            color="accent"
            type="date"
            hide-details
            label="Datum"
          />
        </v-col>
        <v-col v-if="multiDay">
          <date-time-text-field
            v-model="occupancyEnd"
            color="accent"
            hide-details
            type="date"
            label="Enddatum"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <date-time-text-field
            v-model="occupancyStart"
            color="accent"
            hide-details
            type="time"
            label="Start date"
          />
        </v-col>
        <v-col>
          <date-time-text-field
            v-model="occupancyEnd"
            color="accent"
            hide-details
            type="time"
            label="End date"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-checkbox
            v-model="appointmentDiffers"
            color="accent"
            label="Veranstaltungszeit abweichend"
            hide-details
            density="compact"
          />
        </v-col>
      </v-row>
      <v-row v-if="appointmentDiffers">
        <v-col>
          <date-time-text-field
            v-model="appointmentStart"
            color="accent"
            hide-details
            :type="multiDay ? 'datetime-local' : 'time'"
            label="Veranstaltungszeitstart"
          />
        </v-col>
        <v-col>
          <date-time-text-field
            v-model="appointmentEnd"
            color="accent"
            hide-details
            :type="multiDay ? 'datetime-local' : 'time'"
            label="Veranstaltungszeitende"
          />
        </v-col>
      </v-row>
    </template>
  </card-form>
</template>

<script setup lang="ts">
import type { ScheduleTemplate } from "@/api/raumreservierung-backend";

import { computed, ref } from "vue";

import CardForm from "@/components/common/CardForm.vue";
import DateTimeTextField from "@/components/common/date/DateTimeTextField.vue";

const modelValue = defineModel<ScheduleTemplate>({ required: true });

const multiDay = ref<boolean>(false);
const appointmentDiffers = ref<boolean>(false);

const occupancyStart = computed({
  get: () => modelValue.value.occupancyStart,
  set: (val) =>
    (modelValue.value = { ...modelValue.value, occupancyStart: val }),
});

const occupancyEnd = computed({
  get: () => modelValue.value.occupancyEnd,
  set: (val) => (modelValue.value = { ...modelValue.value, occupancyEnd: val }),
});

const appointmentStart = computed({
  get: () => modelValue.value.appointmentStart,
  set: (val) =>
    (modelValue.value = { ...modelValue.value, appointmentStart: val }),
});

const appointmentEnd = computed({
  get: () => modelValue.value.appointmentEnd,
  set: (val) =>
    (modelValue.value = { ...modelValue.value, appointmentEnd: val }),
});
</script>

<style scoped></style>
