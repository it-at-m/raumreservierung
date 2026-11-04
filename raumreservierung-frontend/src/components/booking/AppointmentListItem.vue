<template>
  <v-list-item
    :lines="!occupancyTimeDiffers && !appointmentTimeDiffers ? false : 'two'"
    rounded
    :class="{
      'py-0 mb-n0': !occupancyTimeDiffers && !appointmentTimeDiffers,
    }"
  >
    <template #title>
      <v-row
        style="max-width: 350px"
        no-gutters
        class="align-center mb-1"
      >
        <v-col cols="8">
          <i18n-d
            v-if="!isMultiDay"
            tag="span"
            :value="appointment.schedule.occupancyStart"
            format="short"
          />

          <i18n-t
            v-else
            keypath="common.format.dateRange"
            tag="span"
          >
            <template #start>
              <i18n-d
                :value="appointment.schedule.occupancyStart"
                format="short"
              />
            </template>
            <template #end>
              <i18n-d
                :value="appointment.schedule.occupancyEnd"
                format="short"
              />
            </template>
          </i18n-t>
        </v-col>
        <v-col
          cols="4"
          class="text-center"
        >
          <v-chip
            v-if="occupancyTimeDiffers || appointmentTimeDiffers"
            color="warning"
            size="x-small"
            variant="flat"
          >
            {{ t("components.appointmentListItem.deviatingTimes") }}
          </v-chip>
        </v-col>
      </v-row>
    </template>

    <template #subtitle>
      <v-row
        v-if="occupancyTimeDiffers"
        style="max-width: 350px"
        no-gutters
        class="align-center"
      >
        <v-col
          cols="8"
          class="text-medium-emphasis"
        >
          {{ t("domain.booking.occupancy") }}
        </v-col>
        <v-col
          cols="4"
          class="text-high-emphasis text-center"
        >
          <i18n-t
            keypath="common.format.dateRange"
            tag="span"
          >
            <template #start>
              <i18n-d
                :value="appointment.schedule.occupancyStart"
                format="time"
              />
            </template>
            <template #end>
              <i18n-d
                :value="appointment.schedule.occupancyEnd"
                format="time"
              />
            </template>
          </i18n-t>
        </v-col>
      </v-row>
      <v-row
        v-if="appointmentTimeDiffers"
        style="max-width: 350px"
        no-gutters
        class="align-center mt-1"
      >
        <v-col
          cols="8"
          class="text-medium-emphasis"
        >
          {{ t("domain.booking.appointment") }}
        </v-col>
        <v-col
          cols="4"
          class="text-high-emphasis text-center"
        >
          <i18n-t
            v-if="
              appointment.schedule.appointmentStart &&
              appointment.schedule.appointmentEnd
            "
            keypath="common.format.dateRange"
            tag="span"
          >
            <template #start>
              <i18n-d
                :value="appointment.schedule.appointmentStart"
                format="time"
              />
            </template>
            <template #end>
              <i18n-d
                :value="appointment.schedule.appointmentEnd"
                format="time"
              />
            </template>
          </i18n-t>
        </v-col>
      </v-row>
    </template>
  </v-list-item>
</template>

<script setup lang="ts">
import type {
  AppointmentDetailsResponseDTO,
  ScheduleTemplate,
} from "@/api/raumreservierung-backend";

import { computed } from "vue";
import { useI18n } from "vue-i18n";

import { dateEquals, timeEquals } from "@/util/timeUtil.ts";

const { t } = useI18n();

const { appointment, schedule } = defineProps<{
  appointment: AppointmentDetailsResponseDTO;
  schedule: ScheduleTemplate;
}>();

const occupancyTimeDiffers = computed(
  () =>
    !timeEquals(appointment.schedule.occupancyStart, schedule.occupancyStart) ||
    !timeEquals(appointment.schedule.occupancyEnd, schedule.occupancyEnd)
);

const appointmentTimeDiffers = computed(
  () =>
    !timeEquals(
      appointment.schedule.appointmentStart,
      schedule.appointmentStart
    ) ||
    !timeEquals(appointment.schedule.appointmentEnd, schedule.appointmentEnd)
);

const isMultiDay = computed(
  () =>
    !dateEquals(
      appointment.schedule.occupancyStart,
      appointment.schedule.occupancyEnd
    )
);
</script>

<style scoped></style>
