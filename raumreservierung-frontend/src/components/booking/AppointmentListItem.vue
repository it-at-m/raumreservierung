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
          {{
            useDateFormat(
              appointment.schedule.occupancyStart,
              DATE_FORMAT_DDMMYY
            ).value
          }}
          <span v-if="isMultiDay">
            -
            {{
              useDateFormat(
                appointment.schedule.occupancyEnd,
                DATE_FORMAT_DDMMYY
              ).value
            }}
          </span>
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
            Abweichende Zeit
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
          Belegungszeit:
        </v-col>
        <v-col
          cols="4"
          class="text-high-emphasis text-center"
        >
          {{
            useDateFormat(appointment.schedule.occupancyStart, TIME_FORMAT_HHMM)
              .value
          }}
          -
          {{
            useDateFormat(appointment.schedule.occupancyEnd, TIME_FORMAT_HHMM)
              .value
          }}
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
          Veranstaltungszeit:
        </v-col>
        <v-col
          cols="4"
          class="text-high-emphasis text-center"
        >
          {{
            useDateFormat(
              appointment.schedule.appointmentStart,
              TIME_FORMAT_HHMM
            ).value
          }}
          -
          {{
            useDateFormat(appointment.schedule.appointmentEnd, TIME_FORMAT_HHMM)
              .value
          }}
        </v-col>
      </v-row>
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
import { dateEquals, timeEquals } from "@/util/timeUtil.ts";

const { appointment, schedule } = defineProps<{
  appointment: AppointmentMinimalResponseDTO;
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
