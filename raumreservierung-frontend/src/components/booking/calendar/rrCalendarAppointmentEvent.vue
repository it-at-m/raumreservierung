<template>
  <v-sheet
    class="position-relative w-100 h-100"
    :class="{ 'current-booking': isCurrentBooking }"
    color="transparent"
  >
    <div
      class="text-caption px-1 text-white text-truncate position-relative z-1"
      style="left: 15px"
    >
      {{ event.raw.bookingMinimal.status }}
      {{ event.raw.bookingMinimal.title }}
    </div>

    <div
      v-if="
        event.raw.schedule.appointmentStart && event.raw.schedule.appointmentEnd
      "
      class="position-absolute hatched-overlay rounded"
      style="width: 15px"
      :style="appointmentStyle"
    />
    <div
      class="position-absolute solid-hatched-overlay rounded top-0 h-100"
      style="width: 15px"
    />
  </v-sheet>
</template>

<script setup lang="ts">
import type { AppointmentDetailsResponseDTO } from "@/api/raumreservierung-backend";

import { computed, watch } from "vue";

import { useBookingStatusConfig } from "@/composables/useBookingStatus.ts";

export interface CalendarAppointmentEvent {
  start: Date;
  end: Date;
  category: string;
  timed: boolean;
  raw: AppointmentDetailsResponseDTO;
}

const { event } = defineProps<{
  event: CalendarAppointmentEvent;
  isCurrentBooking: boolean;
}>();

const { config } = useBookingStatusConfig(event.raw.bookingMinimal.status);

const appointmentStyle = computed(() => {
  if (
    !event.raw.schedule.appointmentStart ||
    !event.raw.schedule.appointmentEnd
  ) {
    return undefined;
  }

  const occupancyStart = new Date(event.raw.schedule.occupancyStart).getTime();
  const occupancyEnd = new Date(event.raw.schedule.occupancyEnd).getTime();
  const apptStart = new Date(event.raw.schedule.appointmentStart).getTime();
  const apptEnd = new Date(event.raw.schedule.appointmentEnd).getTime();

  const totalDuration = occupancyEnd - occupancyStart;
  const apptOffset = apptStart - occupancyStart;
  const apptDuration = apptEnd - apptStart;

  if (totalDuration <= 0) {
    return {};
  }

  const topPercent = (apptOffset / totalDuration) * 100;
  const heightPercent = (apptDuration / totalDuration) * 100;

  return {
    top: `${topPercent}%`,
    height: `${heightPercent}%`,
  };
});
</script>

<style scoped>
.solid-hatched-overlay {
  background-color: rgba(255, 255, 255, 0.2);
}

.hatched-overlay {
  background-image: repeating-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.5) 0px,
    rgba(255, 255, 255, 0.5) 10px,
    transparent 11px,
    transparent 21px
  );
}

.current-booking {
  z-index: 4;
  cursor: grab;

  animation: booking-pulse 1s infinite alternate ease-in-out;
}

.current-booking:active {
  cursor: grabbing;
  animation: none;
  box-shadow:
    inset 0 0 0 2px rgba(255, 255, 255, 0.6),
    0 4px 8px rgba(0, 0, 0, 0.3);
  filter: brightness(0.95);
}

@keyframes booking-pulse {
  0% {
    box-shadow:
      inset 0 0 0 1px rgba(255, 255, 255, 0.7),
      0 0 4px rgba(0, 0, 0, 0.1);
    filter: brightness(1);
  }
  100% {
    box-shadow:
      inset 0 0 0 2px rgba(255, 255, 255, 0.3),
      0 0 8px rgba(0, 0, 0, 0.25);
    filter: brightness(1.15);
  }
}
</style>
