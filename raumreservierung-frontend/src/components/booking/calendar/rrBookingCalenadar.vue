<template>
  <div class="mb-4">{{ appointments?.content?.length }}</div>
  <v-sheet height="750px">
    <v-calendar
      color="accent"
      :type="isDayView ? 'category' : 'custom-daily'"
      :start="startDate"
      :end="isDayView ? undefined : endDate"
      :categories="calendarCategories"
      category-text="name"
      :interval-minutes="60"
      :max-days="10"
      event-overlap-mode="column"
      :interval-height="60"
      :first-interval="6"
      :events="localEvents"
      event-color="color"
      @click:event="showEvent"
      @mousedown:event="startDrag"
      @mouseleave="cancelDrag"
      @mousemove:time="mouseMove"
      @mouseup:time="endDrag"
    >
      <template #category="{ category }">
        <div class="text-caption text-center pa-2">
          {{ category.name }}
        </div>
      </template>
      <template #event="{ event }">
        <rr-calendar-appointment-event
          class="v-event-draggable"
          :event="event as unknown as CalendarAppointmentEvent"
          :is-current-booking="event.raw.bookingMinimal.id === booking.id"
        />
      </template>
    </v-calendar>
    <v-menu
      v-model="selectedOpen"
      :activator="selectedElement"
      :close-on-content-click="false"
      location="end"
    >
      <rr-calendar-appointment-popup
        v-if="selectedEvent"
        :appointment="selectedEvent.raw"
      />
    </v-menu>
  </v-sheet>
</template>

<script setup lang="ts">
import type {
  BookingDetailResponseDTO,
  RoomListResponseDTO,
} from "@/api/raumreservierung-backend";
import type { CalendarAppointmentEvent } from "@/components/booking/calendar/rrCalendarAppointmentEvent.vue";
import type { CalendarDayBodySlotScope } from "vuetify/lib/components/VCalendar/types";

import { computed, ref, watch } from "vue";

import RrCalendarAppointmentEvent from "@/components/booking/calendar/rrCalendarAppointmentEvent.vue";
import RrCalendarAppointmentPopup from "@/components/booking/calendar/rrCalendarAppointmentPopup.vue";
import { useGetAppointments } from "@/composables/api/useAppointmentApi.ts";
import { toEndofDay, toStartOfDay } from "@/util/timeUtil.ts";

const { displayedRooms, booking } = defineProps<{
  booking: BookingDetailResponseDTO;
  displayedRooms: RoomListResponseDTO[];
}>();

const selectedOpen = ref<boolean>(false);
const selectedEvent = ref<CalendarAppointmentEvent | undefined>(undefined);
const selectedElement = ref<HTMLElement | undefined>(undefined);

// Events for displayment inside v-calendar
const localEvents = ref<CalendarAppointmentEvent[]>([]);

/**
 * Each room needs to be mapped to id and name for correct displayment
 */
const calendarCategories = computed(() =>
  displayedRooms.map((room) => ({
    name: room.name,
    categoryName: room.id,
  }))
);

const isDayView = computed(() => displayedRooms.length > 1);
const startDate = computed(() => {
  const date = new Date(booking.schedule.occupancyStart);
  if (isDayView.value) {
    return date;
  }

  const day = date.getDay();
  const diff = date.getDate() - day + (day === 0 ? -6 : 1);
  date.setDate(diff);

  return date;
});

const endDate = computed(() => {
  if (isDayView.value) {
    return startDate.value;
  }

  const end = new Date(startDate.value);
  end.setDate(end.getDate() + 9);
  return end;
});

const { data: appointments } = useGetAppointments(() => {
  return {
    startDate: new Date(toStartOfDay(startDate.value)),
    endDate: new Date(toEndofDay(endDate.value)),
    roomIds: displayedRooms
      .map((roomData) => roomData.id)
      .filter((id) => id !== undefined),
    size: 20,
  };
});

/**
 * Watcher for changing localEvents in case of new data from api
 */
watch(
  () => appointments.value?.content,
  (newContent) => {
    if (!newContent) {
      localEvents.value = [];
      return;
    }

    localEvents.value = newContent.map(
      (appointment) =>
        ({
          start: new Date(appointment.schedule.occupancyStart),
          end: new Date(appointment.schedule.occupancyEnd),
          category: appointment.bookingMinimal.roomId,
          timed: true,
          raw: appointment,
        }) as CalendarAppointmentEvent
    );
  },
  { immediate: true }
);

/**
 * Open popup upon clicking on an event with animation
 */
const showEvent = (nativeEvent: Event, payload: { event: unknown }) => {
  if (dragWasPerformed.value) {
    dragWasPerformed.value = false; // Reset für den nächsten regulären Klick
    nativeEvent.stopPropagation();
    return;
  }

  const event = payload.event as CalendarAppointmentEvent;
  const open = () => {
    selectedEvent.value = event;
    selectedElement.value = nativeEvent.target as HTMLElement;
    requestAnimationFrame(() =>
      requestAnimationFrame(() => (selectedOpen.value = true))
    );
  };

  if (selectedOpen.value) {
    selectedOpen.value = false;
    requestAnimationFrame(() => requestAnimationFrame(() => open()));
  } else {
    open();
  }

  nativeEvent.stopPropagation();
};

// ############## Drag and Drop Stuff
interface TMSType {
  year: number;
  month: number;
  day: number;
  hour: number;
  minute: number;
}

const dragEvent = ref<CalendarAppointmentEvent | undefined>(undefined);
const dragTime = ref<number | undefined>(undefined);
const dragWasPerformed = ref<boolean>(false);
const dragOriginalData = ref<
  | {
      start: number;
      end: number;
      category: string;
    }
  | undefined
>(undefined);

const startDrag = (
  nativeEvent: Event,
  payload: { event: unknown; timed: boolean }
) => {
  const event = payload.event as CalendarAppointmentEvent;

  // only current booking shall be moveable
  if (event.raw.bookingMinimal.id === booking.id) {
    dragEvent.value = event;
    dragTime.value = undefined;
    dragWasPerformed.value = false;

    dragOriginalData.value = {
      start: event.start.getTime(),
      end: event.end.getTime(),
      category: event.category,
    };

    nativeEvent.preventDefault();
  }

  if (event && payload.timed) {
    dragEvent.value = event;
    dragTime.value = undefined;
  }
};

const endDrag = (_: Event, payload: CalendarDayBodySlotScope) => {
  if (!dragEvent.value || !dragOriginalData.value) {
    return;
  }

  if (dragWasPerformed.value) {
    // TODO: Emit oder Backend-Call für das Zurückschreiben
    // Die neuen Daten liegen in:
    // dragEvent.value.start
    // dragEvent.value.end
    // dragEvent.value.category (das ist die roomId)
  }

  dragEvent.value = undefined;
  dragTime.value = undefined;
  dragOriginalData.value = undefined;
};

const cancelDrag = () => {
  if (dragEvent.value && dragOriginalData.value) {
    dragEvent.value = {
      ...dragEvent.value,
      start: new Date(dragOriginalData.value.start),
      end: new Date(dragOriginalData.value.end),
      category: dragOriginalData.value.category,
    };
  }

  dragEvent.value = undefined;
  dragTime.value = undefined;
  dragOriginalData.value = undefined;
  dragWasPerformed.value = false;
};

const toTime = (tms: TMSType): number => {
  return new Date(
    tms.year,
    tms.month - 1,
    tms.day,
    tms.hour,
    tms.minute
  ).getTime();
};

/**
 * Round given time to the nearest 15min
 * @param time to be rounded
 * @param down if time should be rounded up or down
 */
const roundTime = (time: number, down = true) => {
  const roundDownTime = 15 * 60 * 1000; // 15 minutes

  return down
    ? time - (time % roundDownTime)
    : time + (roundDownTime - (time % roundDownTime));
};

const mouseMove = (_: Event, payload: CalendarDayBodySlotScope) => {
  if (!dragEvent.value) {
    return;
  }

  const mouse = toTime(payload.tms);

  if (dragTime.value === undefined) {
    dragTime.value = mouse - dragEvent.value.start.getTime();
  } else {
    dragWasPerformed.value = true;
  }
  const duration =
    dragEvent.value.end.getTime() - dragEvent.value.start.getTime();
  const newStartTime = mouse - dragTime.value;
  const newStart = roundTime(newStartTime);

  // TODO: check if below is possible ... this wont trigger any refs but that may be okay
  dragEvent.value = {
    ...dragEvent.value,
    start: new Date(newStart),
    end: new Date(newStart + duration),
    category: payload.category
      ? payload.category.categoryName
      : payload.tms.category.categoryName,
  };

  localEvents.value = [...localEvents.value];
};
</script>
