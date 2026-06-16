<template>
  <v-row>
    <v-col>
      <v-checkbox
        :model-value="multiDay"
        color="accent"
        :label="t('components.scheduleTemplateForm.multiDay')"
        :disabled="disabled"
        hide-details
        density="compact"
        @update:model-value="onMultiDayToggle"
      />
    </v-col>
    <v-col>
      <v-checkbox
        :model-value="wholeDay"
        color="accent"
        label="Ganztägig"
        hide-details
        :disabled="disabled"
        density="compact"
        @update:model-value="onWholeDayToggle"
      />
    </v-col>
    <v-col>
      <slot name="checks" />
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
        :label="t('domain.scheduleTemplate.date')"
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
        :label="t('domain.scheduleTemplate.endDate')"
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
        :label="t('domain.scheduleTemplate.startTime')"
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
        :label="t('domain.scheduleTemplate.endTime')"
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
        :label="t('components.scheduleTemplateForm.appointmentDiffers')"
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
        :label="t('domain.scheduleTemplate.appointmentStart')"
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
        :label="t('domain.scheduleTemplate.appointmentEnd')"
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
import { SCHEDULE_DEFAULT_DURATION } from "@/constants.ts";
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

const wholeDay = computed<boolean>(
  () =>
    occupancyStart.value &&
    occupancyEnd.value &&
    occupancyEnd.value.getHours() === 23 &&
    occupancyEnd.value.getMinutes() === 59 &&
    occupancyStart.value.getHours() <= 7
);

const appointmentDiffers = computed(
  () => !!modelValue.value.appointmentStart && !!modelValue.value.appointmentEnd
);

const occupancyStart = computed({
  get: () => modelValue.value.occupancyStart,
  set: (val) => {
    const updates: Partial<ScheduleTemplate> = { occupancyStart: val };

    if (!multiDay.value && val && occupancyEnd.value) {
      updates.occupancyEnd = syncDatePart(val, occupancyEnd.value);
      if (appointmentDiffers.value) {
        if (appointmentStart.value) {
          updates.appointmentStart = syncDatePart(val, appointmentStart.value);
        }
        if (appointmentEnd.value) {
          updates.appointmentEnd = syncDatePart(val, appointmentEnd.value);
        }
      }
    }

    modelValue.value = { ...modelValue.value, ...updates };
  },
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

const syncDatePart = (source: Date, target: Date) => {
  const baseYear = source.getFullYear();
  const baseMonth = source.getMonth();
  const baseDate = source.getDate();

  const newDate = new Date(target);
  newDate.setFullYear(baseYear, baseMonth, baseDate);
  return newDate;
};

/**
 * If a switch from multiDay to singleDay occurs, we correct the end-dates and the appointment start-date to the occupancyStart-date
 * @param isMulti determines if booking stretches over multiple days
 */
const onMultiDayToggle = async (isMulti: boolean | null) => {
  if (isMulti || !occupancyStart.value) {
    occupancyEnd.value = new Date(
      occupancyEnd.value.setDate(occupancyEnd.value.getDate() + 1)
    );
  } else {
    modelValue.value = {
      ...modelValue.value,
      occupancyEnd: syncDatePart(occupancyStart.value, occupancyEnd.value),
      appointmentStart:
        appointmentDiffers.value && appointmentStart.value
          ? syncDatePart(occupancyStart.value, appointmentStart.value)
          : undefined,
      appointmentEnd:
        appointmentDiffers.value && appointmentEnd.value
          ? syncDatePart(occupancyStart.value, appointmentEnd.value)
          : undefined,
    };
  }
};

const onWholeDayToggle = (isWholeDay: boolean | null) => {
  const referenceDate = occupancyStart.value
    ? new Date(occupancyStart.value)
    : new Date();

  const start = new Date(referenceDate);
  const end = new Date(referenceDate);

  if (isWholeDay) {
    start.setHours(7, 0, 0, 0);
    end.setHours(23, 59, 0, 0);
  } else {
    const now = new Date();
    start.setHours(now.getHours(), now.getMinutes(), 0, 0);
    end.setTime(start.getTime() + SCHEDULE_DEFAULT_DURATION);
  }

  modelValue.value = {
    ...modelValue.value,
    occupancyStart: start,
    occupancyEnd: end,
  };
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
