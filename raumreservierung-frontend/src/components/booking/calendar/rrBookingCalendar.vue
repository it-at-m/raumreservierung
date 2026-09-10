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
    <v-sheet height="730px">
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
            {{ (category as unknown as CalendarCategories).name }}
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

import { mdiChevronLeft, mdiChevronRight } from "@mdi/js";
import { computed, ref, toRef, watch } from "vue";
import { useI18n } from "vue-i18n";

import RrCalendarAppointmentEvent from "@/components/booking/calendar/rrCalendarAppointmentEvent.vue";
import RrCalendarAppointmentPopup from "@/components/booking/calendar/rrCalendarAppointmentPopup.vue";
import { useGetAppointments } from "@/composables/api/useAppointmentApi.ts";
import { useCalendarDragAndDrop } from "@/composables/calendar/useCalendarDragAndDrop.ts";
import { useCalendarNavigation } from "@/composables/calendar/useCalendarNavigation.ts";
import { useCalendarPopup } from "@/composables/calendar/useCalendarPopup.ts";
import { useBookingStatusConfig } from "@/composables/useBookingStatus.ts";
import { FALLBACK_CATEGORY_ROOM } from "@/constants.ts";
import { toEndofDay, toStartOfDay } from "@/util/timeUtil.ts";

interface CalendarCategories {
  name: string;
  categoryName: string;
}

const { t } = useI18n();
const localEvents = ref<CalendarAppointmentEvent[]>([]);
const editedAppointments = ref<Map<string, CalendarAppointmentEvent>>(new Map());

const { displayedRooms, bookingId, roomId, focusDate } = defineProps<{
  focusDate: Date;
  roomId?: string;
  bookingId: string;
  displayedRooms: RoomListResponseDTO[];
}>();

const emit = defineEmits<{
  updatedSchedule: [event: CalendarAppointmentEvent];
}>();

const { resolveColor } = useBookingStatusConfig();

const calendarCategories = computed(() => {
  const categories = displayedRooms.map(
    (room) =>
      ({
        name: room.name,
        categoryName: room.id,
      }) as CalendarCategories,
  );

  if (!roomId) {
    categories.push(FALLBACK_CATEGORY_ROOM);
  }

  return categories;
});

const categoriesCount = computed(() => calendarCategories.value.length);

const { isDayView, startDate, endDate, isFocusedWeek, calendarTitle, next, prev, jumpToBooking } =
  useCalendarNavigation(
    toRef(() => focusDate),
    categoriesCount,
  );

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

watch(
  [() => appointments.value?.content, () => bookingAppointments.value?.content],
  () => {
    buildLocalEvents();
  },
  { immediate: true },
);
// --- Ende lokaler State ---

const { dragWasPerformed, startDrag, endDrag, cancelDrag, mouseMove } = useCalendarDragAndDrop(
  toRef(() => bookingId),
  localEvents,
  (dragEvent) => {
    editedAppointments.value.set(dragEvent.raw.id, {
      ...dragEvent,
      start: new Date(dragEvent.start),
      end: new Date(dragEvent.end),
    });
    emit("updatedSchedule", dragEvent);
    buildLocalEvents();
  },
);

const { selectedOpen, selectedEvent, selectedElement, showEvent } =
  useCalendarPopup(dragWasPerformed);

const getEventColor = (event: unknown): string => {
  const eventPayload = event as CalendarAppointmentEvent;
  return resolveColor(eventPayload.raw.bookingMinimal.status);
};

defineExpose({
  resetAppointments: resetAppointmentsAndEdits,
});
</script>
