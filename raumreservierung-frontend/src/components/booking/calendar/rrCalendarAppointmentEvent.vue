<template>
  <v-sheet
    class="position-relative w-100 h-100"
    :class="{ 'current-booking': isCurrentBooking }"
    color="transparent"
  >
    <div
      class="text-caption px-1 text-white text-truncate position-relative z-1 text-offset"
    >
      {{ event.raw.bookingMinimal.status }}
      {{ event.raw.bookingMinimal.title }}
    </div>
    <div
      class="position-absolute top-0 h-100 status-bar d-flex flex-column rounded overflow-hidden"
    >
      <template v-if="segmentHeights">
        <div
          class="overlay-hatched w-100 rounded"
          :style="{ height: segmentHeights.before }"
        />
        <div
          class="overlay-solid w-100 rounded"
          :style="{ height: segmentHeights.appt }"
        />
        <div
          class="overlay-hatched w-100 rounded"
          :style="{ height: segmentHeights.after }"
        />
      </template>
      <template v-else>
        <div class="overlay-solid w-100 h-100" />
      </template>
    </div>
  </v-sheet>
</template>

<script setup lang="ts">
import type { AppointmentDetailsResponseDTO } from "@/api/raumreservierung-backend";

import { computed } from "vue";

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

const segmentHeights = computed(() => {
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

  if (totalDuration <= 0) {
    return undefined;
  }

  const validApptStart = Math.max(occupancyStart, apptStart);
  const validApptEnd = Math.min(occupancyEnd, apptEnd);

  const beforeDuration = validApptStart - occupancyStart;
  const apptDuration = validApptEnd - validApptStart;
  const afterDuration = occupancyEnd - validApptEnd;

  return {
    before: `${(beforeDuration / totalDuration) * 100}%`,
    appt: `${(apptDuration / totalDuration) * 100}%`,
    after: `${(afterDuration / totalDuration) * 100}%`,
  };
});
</script>

<style scoped>
.text-offset {
  left: 15px;
}

.status-bar {
  width: 15px;
}

.overlay-solid {
  background-color: rgba(255, 255, 255, 0.4);
}

.overlay-hatched {
  background-image: repeating-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.5) 0px,
    rgba(255, 255, 255, 0.5) 4px,
    transparent 4px,
    transparent 8px
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
