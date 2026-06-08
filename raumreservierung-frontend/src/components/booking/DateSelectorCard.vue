<template>
  <card-form :subtitle="t('components.dateSelectorCard.subtitle')">
    <template #text>
      <v-row>
        <v-checkbox
          v-model="multiDay"
          color="accent"
          :label="t('components.scheduleTemplateForm.multiDay')"
          hide-details
          density="compact"
          @update:model-value="onMultiDayToggle"
        />
      </v-row>

      <v-row>
        <v-col>
          <date-time-text-field
            v-model="occupancyStart"
            color="accent"
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
            v-model="appointmentDiffers"
            color="accent"
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
            hide-details="auto"
            :append-inner-icon="multiDay ? '' : mdiClockOutline"
            :type="multiDay ? 'datetime-local' : 'time'"
            :label="t('domain.scheduleTemplate.appointmentEnd')"
            :rules="[requiredRule, validateApptEndWithinOccupancy]"
          />
        </v-col>
      </v-row>
    </template>
  </card-form>
</template>

<script setup lang="ts">
import type { ScheduleTemplate } from "@/api/raumreservierung-backend";

import { mdiClockOutline } from "@mdi/js";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";

import CardForm from "@/components/common/CardForm.vue";
import DateTimeTextField from "@/components/common/date/DateTimeTextField.vue";
import { useRules } from "@/composables/useRules";
import { dateEquals } from "@/util/timeUtil.ts";

const { t } = useI18n();
const rules = useRules();

const modelValue = defineModel<ScheduleTemplate>({ required: true });

const multiDay = ref<boolean>(
  modelValue.value.occupancyStart &&
    modelValue.value.occupancyEnd &&
    !dateEquals(modelValue.value.occupancyStart, modelValue.value.occupancyEnd)
);
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

/**
 * If a switch from multiDay to singleDay occurs, we correct the end-dates and the appointment start-date to the occupancyStart-date
 * @param isMulti determines if booking stretches over multiple days
 */
const onMultiDayToggle = (isMulti: boolean | null) => {
  if (isMulti || !occupancyStart.value) {
    return;
  }

  const baseYear = occupancyStart.value.getFullYear();
  const baseMonth = occupancyStart.value.getMonth();
  const baseDate = occupancyStart.value.getDate();

  const syncDatePart = (target: Date) => {
    const newDate = new Date(target);
    newDate.setFullYear(baseYear, baseMonth, baseDate);
    return newDate;
  };

  if (occupancyEnd.value) {
    occupancyEnd.value = syncDatePart(occupancyEnd.value);
  }
  if (appointmentDiffers.value && appointmentStart.value) {
    appointmentStart.value = syncDatePart(appointmentStart.value);
  }
  if (appointmentDiffers.value && appointmentEnd.value) {
    appointmentEnd.value = syncDatePart(appointmentEnd.value);
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
