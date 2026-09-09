<template>
  <div class="d-flex flex-column h-100">
    <v-sheet>
      <v-toolbar flat>
        <v-btn :icon="mdiChevronLeft" variant="text" @click="prev" />
        <v-toolbar-title>{{ calendarTitle }}</v-toolbar-title>
        <v-spacer />
        <v-btn
          v-if="!isFocusedWeek"
          class="mr-2"
          variant="tonal"
          :text="t('components.rrBookingCalendar.toBooking')"
          @click="jumpToBooking"
        />
        <v-btn :icon="mdiChevronRight" variant="text" @click="next" />
      </v-toolbar>
    </v-sheet>
    <v-sheet height="750px">
      <v-calendar
        color="accent"
        :type="isDayView ? 'category' : 'custom-daily'"
        :start="startDate"
        :end="isDayView ? undefined : endDate"
        :categories="calendarCategories"
        category-text="name"
        category-show-all
        :interval-minutes="60"
        :max-days="10"
        event-overlap-mode="column"
        :interval-height="60"
        :events="localEvents"
        :event-color="getEventColor"
        @click:event="showEvent"
        @mousedown:event="startDrag"
        @mouseleave="cancelDrag"
        @mousemove:time="mouseMove"
        @mousemove:time-category="mouseMove"
        @mouseup:time="endDrag"
      >
        <template #category="{ category }">
          <div class="text-caption text-center pa-2">
            {{ category.name }}
          </div>
        </template>
        <template #event="{ event }">
          <rr-calendar-appointment-event
            :id="event.raw.id"
            class="v-event-draggable"
            :event="event as unknown as CalendarAppointmentEvent"
            :is-current-booking="event.raw.bookingMinimal.id === bookingId"
          />
        </template>
      </v-calendar>
      <v-menu
        v-model="selectedOpen"
        :activator="selectedElement"
        :close-on-content-click="false"
        location="end"
      >
        <rr-calendar-appointment-popup v-if="selectedEvent" :appointment="selectedEvent.raw" />
      </v-menu>
    </v-sheet>
  </div>
</template>

<script setup lang="ts">
import type { RoomListResponseDTO } from "@/api/raumreservierung-backend";
import type { CalendarAppointmentEvent } from "@/components/booking/calendar/rrCalendarAppointmentEvent.vue";
import type { CalendarDayBodySlotScope } from "vuetify/lib/components/VCalendar/types";

import { mdiChevronLeft, mdiChevronRight } from "@mdi/js";
import { computed, ref, watch } from "vue";

import RrCalendarAppointmentEvent from "@/components/booking/calendar/rrCalendarAppointmentEvent.vue";
import RrCalendarAppointmentPopup from "@/components/booking/calendar/rrCalendarAppointmentPopup.vue";
import { useGetAppointments } from "@/composables/api/useAppointmentApi.ts";
import { useBookingStatusConfig } from "@/composables/useBookingStatus.ts";
import { dateEquals, toEndofDay, toStartOfDay } from "@/util/timeUtil.ts";
import { useI18n } from "vue-i18n";
import { FALLBACK_CATEGORY_ROOM } from "@/constants.ts";

const { t } = useI18n();

const { displayedRooms, bookingId, roomId, focusDate } = defineProps<{
  focusDate: Date;
  roomId?: string;
  bookingId: string;
  displayedRooms: RoomListResponseDTO[];
}>();

const { resolveColor } = useBookingStatusConfig();

const currentCalendarDate = ref<Date>(new Date(focusDate));

const selectedOpen = ref<boolean>(false);
const selectedEvent = ref<CalendarAppointmentEvent | undefined>(undefined);
const selectedElement = ref<HTMLElement | undefined>(undefined);
// Events for displayment inside v-calendar
const localEvents = ref<CalendarAppointmentEvent[]>([]);

// Local edited events
const editedAppointments = ref<Map<string, CalendarAppointmentEvent>>(new Map());

const emit = defineEmits<{
  updatedSchedule: [event: CalendarAppointmentEvent];
}>();

/**
 * Each room needs to be mapped to id and name for correct displayment
 */
const calendarCategories = computed(() => {
  const categories = displayedRooms.map((room) => ({
    name: room.name,
    categoryName: room.id,
  }));

  if (!roomId) {
    categories.push(FALLBACK_CATEGORY_ROOM);
  }

  return categories;
});

const isDayView = computed(() => calendarCategories.value.length > 1);
const startDate = computed(() => {
  const date = new Date(currentCalendarDate.value);
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

const isFocusedWeek = computed(() => dateEquals(currentCalendarDate.value, focusDate));

const calendarTitle = computed(() => {
  return startDate.value.toLocaleDateString("de-DE", {
    month: "long",
    year: "numeric",
  });
});

const next = () => {
  const newDate = new Date(currentCalendarDate.value);
  newDate.setDate(newDate.getDate() + (isDayView.value ? 1 : 7));
  currentCalendarDate.value = newDate;
};

const prev = () => {
  const newDate = new Date(currentCalendarDate.value);
  newDate.setDate(newDate.getDate() - (isDayView.value ? 1 : 7));
  currentCalendarDate.value = newDate;
};

const jumpToBooking = () => {
  currentCalendarDate.value = new Date(focusDate);
};

const { data: appointments, refetch: resetAppointments } = useGetAppointments(() => {
  return {
    startDate: new Date(toStartOfDay(startDate.value)),
    endDate: new Date(toEndofDay(endDate.value)),
    roomIds: displayedRooms.map((roomData) => roomData.id).filter((id) => id !== undefined),
    size: 20,
  };
});

const { data: bookingAppointments } = useGetAppointments(() => {
  return roomId
    ? undefined
    : {
        bookingId: bookingId,
        startDate: new Date(toStartOfDay(startDate.value)),
        endDate: new Date(toEndofDay(endDate.value)),
      };
});

const buildLocalEvents = () => {
  const events: CalendarAppointmentEvent[] = [];
  const newAppointments = appointments.value?.content;
  const newBookingAppointments = bookingAppointments.value?.content;

  if (newAppointments) {
    events.push(
      ...newAppointments.map(
        (appointment) =>
          ({
            start: new Date(appointment.schedule.occupancyStart),
            end: new Date(appointment.schedule.occupancyEnd),
            category: appointment.bookingMinimal.roomId,
            timed: true,
            raw: appointment,
          }) as CalendarAppointmentEvent,
      ),
    );
  }

  if (newBookingAppointments) {
    events.push(
      ...newBookingAppointments.map(
        (appointment) =>
          ({
            start: new Date(appointment.schedule.occupancyStart),
            end: new Date(appointment.schedule.occupancyEnd),
            category: FALLBACK_CATEGORY_ROOM.categoryName,
            timed: true,
            raw: appointment,
          }) as CalendarAppointmentEvent,
      ),
    );
  }

  editedAppointments.value.forEach((editedEvent, id) => {
    const index = events.findIndex((e) => e.raw.id === id);
    if (index !== -1) {
      events[index] = editedEvent;
    } else {
      events.push(editedEvent);
    }
  });

  localEvents.value = events;
};

const resetAppointmentsAndEdits = () => {
  editedAppointments.value.clear();
  jumpToBooking();
  resetAppointments();
  buildLocalEvents();
};

defineExpose({
  resetAppointments: resetAppointmentsAndEdits,
});

/**
 * Watcher for changing localEvents in case of new data from api
 */
watch(
  [() => appointments.value?.content, () => bookingAppointments.value?.content],
  ([newAppointments, newBookingAppointments]) => {
    buildLocalEvents();
  },
  { immediate: true },
);

/**
 * Open popup upon clicking on an event with animation
 */
const showEvent = (nativeEvent: Event, payload: { event: unknown }) => {
  if (dragWasPerformed.value) {
    dragWasPerformed.value = false;
    nativeEvent.stopPropagation();
    return;
  }

  const event = payload.event as CalendarAppointmentEvent;
  const open = () => {
    selectedEvent.value = event;
    selectedElement.value = nativeEvent.target as HTMLElement;
    requestAnimationFrame(() => requestAnimationFrame(() => (selectedOpen.value = true)));
  };

  if (selectedOpen.value) {
    selectedOpen.value = false;
    requestAnimationFrame(() => requestAnimationFrame(() => open()));
  } else {
    open();
  }

  nativeEvent.stopPropagation();
};

const getEventColor = (event: unknown): string => {
  const eventPayload = event as CalendarAppointmentEvent;

  return resolveColor(eventPayload.raw.bookingMinimal.status);
};

// ############## Drag and Drop Stuff
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

const startDrag = (nativeEvent: Event, payload: { event: unknown; timed: boolean }) => {
  const payloadEvent = payload.event as CalendarAppointmentEvent;

  if (payloadEvent.raw.bookingMinimal.id == bookingId && payload.timed) {
    const realEvent = localEvents.value.find((e) => e.raw.id === payloadEvent.raw.id);

    if (realEvent) {
      dragEvent.value = realEvent;
      dragTime.value = undefined;
      dragWasPerformed.value = false;

      dragOriginalData.value = {
        start: realEvent.start.getTime(),
        end: realEvent.end.getTime(),
        category: realEvent.category,
      };

      nativeEvent.preventDefault();
    }
  }
};

const endDrag = () => {
  if (!dragEvent.value || !dragOriginalData.value) {
    return;
  }

  if (dragWasPerformed.value) {
    editedAppointments.value.set(dragEvent.value.raw.id, {
      ...dragEvent.value,
      start: new Date(dragEvent.value.start),
      end: new Date(dragEvent.value.end),
    });

    emit("updatedSchedule", dragEvent.value);

    buildLocalEvents();
  }

  dragEvent.value = undefined;
  dragTime.value = undefined;
  dragOriginalData.value = undefined;
};

const cancelDrag = () => {
  if (dragEvent.value && dragOriginalData.value) {
    dragEvent.value.start = new Date(dragOriginalData.value.start);
    dragEvent.value.end = new Date(dragOriginalData.value.end);
    dragEvent.value.category = dragOriginalData.value.category;
  }

  dragEvent.value = undefined;
  dragTime.value = undefined;
  dragOriginalData.value = undefined;
  dragWasPerformed.value = false;
};

const toTime = (tms: CalendarDayBodySlotScope): number => {
  return new Date(tms.year, tms.month - 1, tms.day, tms.hour, tms.minute).getTime();
};

/**
 * Round given time to the nearest 15min
 * @param time to be rounded
 * @param down if time should be rounded up or down
 */
const roundTime = (time: number, down = true) => {
  const roundDownTime = 15 * 60 * 1000; // 15 minutes

  return down ? time - (time % roundDownTime) : time + (roundDownTime - (time % roundDownTime));
};

const mouseMove = (_: Event, payload: CalendarDayBodySlotScope) => {
  if (!dragEvent.value || !dragOriginalData.value) {
    return;
  }

  const mouse = toTime(payload);

  if (dragTime.value === undefined) {
    dragTime.value = mouse - dragEvent.value.start.getTime();
  } else {
    dragWasPerformed.value = true;
  }

  const duration = dragOriginalData.value.end - dragOriginalData.value.start;
  const newStartTime = mouse - dragTime.value;
  const newStart = roundTime(newStartTime);

  dragEvent.value.start = new Date(newStart);
  dragEvent.value.end = new Date(newStart + duration);

  const targetCategory = payload.category?.categoryName;
  if (targetCategory) {
    dragEvent.value.category = targetCategory;
  }
};
</script>
