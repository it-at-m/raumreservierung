<template>
  <v-card
    class="overflow-hidden"
    min-width="250px"
    max-width="600px"
  >
    <v-sheet
      :color="config.color"
      class="pa-4 d-flex align-center"
    >
      <span class="text-h6">{{ appointment.bookingMinimal.title }}</span>
      <v-spacer />
      <v-icon
        :icon="config.icon"
        size="small"
        class="ml-2"
      />
    </v-sheet>
    <v-card-text class="pt-4">
      <icon-information
        :icon="mdiAccountOutline"
        :text="
          t('common.format.fullName', {
            firstName: appointment.bookingMinimal.bookedBy.firstName,
            lastName: appointment.bookingMinimal.bookedBy.lastName,
          })
        "
      />
      <icon-information
        v-if="appointment.schedule.occupancyStart"
        class="my-2"
        :icon="mdiCalendarStartOutline"
        :text="toDateString(appointment.schedule.occupancyStart)"
      />
      <icon-information
        v-if="
          appointment.schedule.occupancyStart &&
          appointment.schedule.occupancyEnd
        "
        class="my-2"
        :icon="mdiCalendarRangeOutline"
        :text="
          t('common.format.dateRange', {
            start: toTimeString(appointment.schedule.occupancyStart),
            end: toTimeString(appointment.schedule.occupancyEnd),
          })
        "
      />
    </v-card-text>
    <v-divider />
    <v-card-actions>
      <v-icon-btn
        v-tooltip:top-start="{
          text: t('generics.view', { domain: t('domain.booking.header') }),
        }"
        :icon="mdiInformationOutline"
        variant="tonal"
        @click="
          router.push({
            name: ROUTES.BOOKINGS_DETAILS,
            params: {
              id: appointment.bookingMinimal.id,
            },
          })
        "
      />
      <v-spacer />
      <v-icon-btn
        v-tooltip:top-start="{
          text: t('generics.calendarEdit', {
            domain: t('domain.booking.header'),
          }),
        }"
        :icon="mdiCalendarEditOutline"
        variant="tonal"
        @click="
          router.push({
            name: ROUTES.BOOKINGS_CALENDAR,
            params: {
              id: appointment.bookingMinimal.id,
            },
          })
        "
      />
      <v-icon-btn
        v-tooltip:top-start="{
          text: t('generics.edit', { domain: t('domain.booking.header') }),
        }"
        :icon="mdiPencil"
        variant="tonal"
        @click="
          router.push({
            name: ROUTES.BOOKINGS_EDIT,
            params: {
              id: appointment.bookingMinimal.id,
            },
          })
        "
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { AppointmentDetailsResponseDTO } from "@/api/raumreservierung-backend";

import {
  mdiAccountOutline,
  mdiCalendarEditOutline,
  mdiCalendarRangeOutline,
  mdiCalendarStartOutline,
  mdiInformationOutline,
  mdiPencil,
} from "@mdi/js";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

import IconInformation from "@/components/common/IconInformation.vue";
import { useBookingStatusConfig } from "@/composables/useBookingStatus.ts";
import { ROUTES } from "@/types/Routes.ts";
import { toDateString, toTimeString } from "@/util/formatter.ts";

const router = useRouter();

const { t } = useI18n();

const { appointment } = defineProps<{
  appointment: AppointmentDetailsResponseDTO;
}>();

const { config } = useBookingStatusConfig(
  () => appointment.bookingMinimal.status
);
</script>

<style scoped></style>
