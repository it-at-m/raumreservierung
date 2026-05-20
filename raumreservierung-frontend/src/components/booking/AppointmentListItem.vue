<template>
  <v-list-item>
    <template #title>
      <span>
        {{
          useDateFormat(appointment.schedule.occupancyStart, DATE_FORMAT_DDMMYY)
        }}
      </span>
      <span v-if="isMultiDay">
        -
        {{
          useDateFormat(appointment.schedule.occupancyEnd, DATE_FORMAT_DDMMYY)
        }}
      </span>
    </template>
    <template #subtitle>
      <v-list-item-subtitle v-if="occupancyTimeDiffers">
        <span>OccupancyTime: </span>
        <span
          >{{
            useDateFormat(appointment.schedule.occupancyStart, TIME_FORMAT_HHMM)
          }}
          -
          {{
            useDateFormat(appointment.schedule.occupancyEnd, TIME_FORMAT_HHMM)
          }}</span
        >
      </v-list-item-subtitle>
      <v-list-item-subtitle v-if="appointmentTimeDiffers">
        <span>AppointmentTime: </span>
        <span
          >{{
            useDateFormat(
              appointment.schedule.appointmentStart,
              TIME_FORMAT_HHMM
            )
          }}
          -
          {{
            useDateFormat(appointment.schedule.appointmentEnd, TIME_FORMAT_HHMM)
          }}</span
        >
      </v-list-item-subtitle>
    </template>
  </v-list-item>
</template>

<script setup lang="ts">
import type {
  AppointmentMinimalResponseDTO,
  ScheduleTemplate,
} from "@/api/raumreservierung-backend";

import { useDateFormat } from "@vueuse/core";
import { computed } from "vue";

import { DATE_FORMAT_DDMMYY, TIME_FORMAT_HHMM } from "@/constants.ts";

const { appointment, schedule } = defineProps<{
  appointment: AppointmentMinimalResponseDTO;
  schedule: ScheduleTemplate;
}>();

const occupancyTimeDiffers = computed(
  () =>
    appointment.schedule.occupancyStart?.getMinutes() !==
      schedule.occupancyStart?.getMinutes() ||
    appointment.schedule.occupancyEnd?.getMinutes() !==
      schedule.occupancyEnd?.getMinutes() ||
    appointment.schedule.occupancyStart?.getHours() !==
      schedule.occupancyStart?.getHours() ||
    appointment.schedule.occupancyEnd?.getHours() !==
      schedule.occupancyEnd?.getHours()
);

const appointmentTimeDiffers = computed(
  () =>
    appointment.schedule.appointmentStart?.getMinutes() !==
      schedule.appointmentStart?.getMinutes() ||
    appointment.schedule.appointmentEnd?.getMinutes() !==
      schedule.appointmentEnd?.getMinutes() ||
    appointment.schedule.appointmentStart?.getHours() !==
      schedule.appointmentStart?.getHours() ||
    appointment.schedule.appointmentEnd?.getHours() !==
      schedule.appointmentEnd?.getHours()
);

const isMultiDay = computed(
  () =>
    appointment.schedule.occupancyStart.getDate() !==
      appointment.schedule.occupancyEnd.getDate() ||
    appointment.schedule.occupancyStart.getMonth() !==
      appointment.schedule.occupancyEnd.getMonth() ||
    appointment.schedule.occupancyStart.getFullYear() !==
      appointment.schedule.occupancyEnd.getFullYear()
);
</script>

<style scoped></style>
