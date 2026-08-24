<template>
  <v-text-field
    v-bind="$attrs"
    :type="type"
    :model-value="displayValue"
    @update:model-value="updateDate"
  >
    <template #message="{ message: vTextMessage }">
      <div class="d-flex align-center">
        <v-icon
          class="mr-1"
          :icon="mdiInformationOutline"
        />
        <span>{{ vTextMessage }}</span>
      </div>
    </template>
  </v-text-field>
</template>

<script setup lang="ts">
import { mdiInformationOutline } from "@mdi/js";
import { useDateFormat } from "@vueuse/core";
import { computed } from "vue";

import {
  DATE_FORMAT_YYYYMMDD,
  DATE_TIME_FORMAT_YYYYMMDDTHHMM,
  TIME_FORMAT_HHMM,
} from "@/constants.ts";

// datetime-local is chosen as the default case as it edits the whole date-objekt
const { type = "datetime-local" } = defineProps<{
  type?: "date" | "time" | "datetime-local";
  message?: string;
}>();

const modelValue = defineModel<Date>();

const format = computed(() => {
  switch (type) {
    case "time":
      return TIME_FORMAT_HHMM;
    case "date":
      return DATE_FORMAT_YYYYMMDD;
    default:
      return DATE_TIME_FORMAT_YYYYMMDDTHHMM;
  }
});

const dateFormat = useDateFormat(modelValue, format);

const displayValue = computed(() => (modelValue.value ? dateFormat.value : ""));

const applyDatePart = (dateObj: Date, dateString: string) => {
  const [year, month, day] = dateString.split("-").map(Number);

  if (year && month && day) {
    dateObj.setFullYear(year, month - 1, day);
  }
};

const applyTimePart = (dateObj: Date, timeString: string) => {
  const [hour, minute] = timeString.split(":").map(Number);

  // do not shorten the '!== undefined' - otherwise a '0' might will lead to a false statement!
  if (
    hour !== undefined &&
    !isNaN(hour) &&
    minute !== undefined &&
    !isNaN(minute)
  ) {
    dateObj.setHours(hour, minute);
  }
};

const updateDate = (value: string | undefined | null) => {
  if (!value) {
    return;
  }

  const baseDate = new Date((modelValue.value ?? new Date()).toISOString());

  switch (type) {
    case "date": {
      applyDatePart(baseDate, value);
      break;
    }
    case "time": {
      applyTimePart(baseDate, value);
      break;
    }
    default: {
      const [datePart, timePart] = value.split("T");

      if (datePart && timePart) {
        applyDatePart(baseDate, datePart);
        applyTimePart(baseDate, timePart);
      }
    }
  }

  modelValue.value = baseDate;
};
</script>
