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
          <span v-if="!isMultiDay">
            {{
              t("common.format.dateSingle", {
                date: useDateFormat(
                  appointment.schedule.occupancyStart,
                  DATE_FORMAT_DDMMYY
                ).value,
              })
            }}
          </span>

          <span v-else>
            {{
              t("common.format.dateRange", {
                start: useDateFormat(
                  appointment.schedule.occupancyStart,
                  DATE_FORMAT_DDMMYY
                ).value,
                end: useDateFormat(
                  appointment.schedule.occupancyEnd,
                  DATE_FORMAT_DDMMYY
                ).value,
              })
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
          {{
            t("common.format.dateRange", {
              start: useDateFormat(
                appointment.schedule.occupancyStart,
                TIME_FORMAT_HHMM
              ).value,
              end: useDateFormat(
                appointment.schedule.occupancyEnd,
                TIME_FORMAT_HHMM
              ).value,
            })
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
          {{ t("domain.booking.appointment") }}
        </v-col>
        <v-col
          cols="4"
          class="text-high-emphasis text-center"
        >
          {{
            t("common.format.dateRange", {
              start: useDateFormat(
                appointment.schedule.appointmentStart,
                TIME_FORMAT_HHMM
              ).value,
              end: useDateFormat(
                appointment.schedule.appointmentEnd,
                TIME_FORMAT_HHMM
              ).value,
            })
          }}
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

import { useDateFormat } from "@vueuse/core";
import { computed } from "vue";
import { useI18n } from "vue-i18n";

import { DATE_FORMAT_DDMMYY, TIME_FORMAT_HHMM } from "@/constants.ts";
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
