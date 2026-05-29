<template>
  <v-row>
    <v-col>
      <v-checkbox
        :model-value="multiDay"
        color="accent"
        label="Mehrtägiger Termin"
        :disabled="disabled"
        hide-details
        density="compact"
        @update:model-value="onMultiDayToggle"
      />
    </v-col>
  </v-row>

  <v-row>
    <v-col>
      <date-time-text-field
        v-model="occupancyStart"
        color="accent"
        :disabled="disabled"
        type="date"
        hide-details="auto"
        label="Datum"
        :rules="[requiredRule]"
      />
    </v-col>
    <v-col v-if="multiDay">
      <date-time-text-field
        v-model="occupancyEnd"
        color="accent"
        hide-details="auto"
        :disabled="disabled"
        type="date"
        label="Enddatum"
        :rules="[requiredRule]"
      />
    </v-col>
  </v-row>

  <v-row>
    <v-col>
      <date-time-text-field
        v-model="occupancyStart"
        :append-inner-icon="mdiClockOutline"
        color="accent"
        hide-details="auto"
        :disabled="disabled"
        type="time"
        label="Start date"
        :rules="[requiredRule]"
      />
    </v-col>
    <v-col>
      <date-time-text-field
        v-model="occupancyEnd"
        :append-inner-icon="mdiClockOutline"
        color="accent"
        :disabled="disabled"
        hide-details="auto"
        type="time"
        label="End date"
        :rules="[requiredRule, validateOccupancyEndAfterStart]"
      />
    </v-col>
  </v-row>

  <v-row>
    <v-col>
      <v-checkbox
        :model-value="appointmentDiffers"
        color="accent"
        :disabled="disabled"
        label="Veranstaltungszeit abweichend"
        hide-details
        density="compact"
        @update:model-value="onAppointmentDiffers"
      />
    </v-col>
  </v-row>

  <v-row v-if="appointmentDiffers">
    <v-col>
      <date-time-text-field
        v-model="appointmentStart"
        color="accent"
        :disabled="disabled"
        hide-details="auto"
        :append-inner-icon="multiDay ? '' : mdiClockOutline"
        :type="multiDay ? 'datetime-local' : 'time'"
        label="Veranstaltungszeitstart"
        :rules="[requiredRule, validateApptStartWithinOccupancy]"
      />
    </v-col>
    <v-col>
      <date-time-text-field
        v-model="appointmentEnd"
        color="accent"
        :disabled="disabled"
        hide-details="auto"
        :append-inner-icon="multiDay ? '' : mdiClockOutline"
        :type="multiDay ? 'datetime-local' : 'time'"
        label="Veranstaltungszeitende"
        :rules="[requiredRule, validateApptEndWithinOccupancy]"
      />
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import type { ScheduleTemplate } from "@/api/raumreservierung-backend";

import { mdiClockOutline } from "@mdi/js";
import { computed } from "vue";
import { useI18n } from "vue-i18n";

import DateTimeTextField from "@/components/common/date/DateTimeTextField.vue";
import { useRules } from "@/composables/useRules";
import { dateEquals } from "@/util/timeUtil.ts";

const { t } = useI18n();
const rules = useRules();

const modelValue = defineModel<ScheduleTemplate>({ required: true });

const { disabled = false } = defineProps<{
  disabled?: boolean;
}>();

const multiDay = computed<boolean>(
  () =>
    modelValue.value.occupancyStart &&
    modelValue.value.occupancyEnd &&
    !dateEquals(modelValue.value.occupancyStart, modelValue.value.occupancyEnd)
);

const appointmentDiffers = computed(
  () => !!modelValue.value.appointmentStart && !!modelValue.value.appointmentEnd
);

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

/**
 * If a switch from multiDay to singleDay occurs, we correct the end-dates and the appointment start-date to the occupancyStart-date
 * @param isMulti determines if booking stretches over multiple days
 */
const onMultiDayToggle = (isMulti: boolean | null) => {
  const baseYear = occupancyStart.value.getFullYear();
  const baseMonth = occupancyStart.value.getMonth();
  const baseDate = occupancyStart.value.getDate();

  const syncDatePart = (target: Date) => {
    const newDate = new Date(target);
    newDate.setFullYear(baseYear, baseMonth, baseDate);
    return newDate;
  };
  if (isMulti || !occupancyStart.value) {
    occupancyEnd.value = new Date(
      occupancyEnd.value.setDate(occupancyEnd.value.getDate() + 1)
    );
  } else {
    if (occupancyEnd.value) {
      occupancyEnd.value = syncDatePart(occupancyEnd.value);
    }
    if (appointmentDiffers.value && appointmentStart.value) {
      appointmentStart.value = syncDatePart(appointmentStart.value);
    }
    if (appointmentDiffers.value && appointmentEnd.value) {
      appointmentEnd.value = syncDatePart(appointmentEnd.value);
    }
  }
};

const onAppointmentDiffers = (apptDiffers: boolean | null) => {
  const apptStart = !apptDiffers
    ? undefined
    : occupancyStart.value
      ? occupancyStart.value
      : new Date();

  const apptEnd = !apptDiffers
    ? undefined
    : occupancyStart.value
      ? occupancyStart.value
      : new Date();

  modelValue.value = {
    ...modelValue.value,
    appointmentStart: apptStart,
    appointmentEnd: apptEnd,
  };
};

const requiredRule = rules.required(t("common.rules.notEmpty"));

const validateOccupancyEndAfterStart = () =>
  !occupancyStart.value ||
  !occupancyEnd.value ||
  occupancyEnd.value >= occupancyStart.value ||
  t("common.rules.endBeforeStart");

const validateApptStartWithinOccupancy = () =>
  !appointmentStart.value ||
  !occupancyStart.value ||
  appointmentStart.value >= occupancyStart.value ||
  t("common.rules.apptStartBeforeOccupancy");

const validateApptEndWithinOccupancy = () =>
  !appointmentEnd.value ||
  !occupancyEnd.value ||
  appointmentEnd.value <= occupancyEnd.value ||
  t("common.rules.apptEndAfterOccupancy");
</script>

<style scoped></style>
