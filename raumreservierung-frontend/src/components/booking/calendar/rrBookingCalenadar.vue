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
      :events="mappedEvents"
      event-color="color"
      @click:event="showEvent"
    >
      <template #category="{ category }">
        <div class="text-caption text-center pa-2">
          {{ category.name }}
        </div>
      </template>
      <template #event="{ event }">
        <rr-calendar-appointment-event
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

import { computed, ref } from "vue";

import RrCalendarAppointmentEvent from "@/components/booking/calendar/rrCalendarAppointmentEvent.vue";
import RrCalendarAppointmentPopup from "@/components/booking/calendar/rrCalendarAppointmentPopup.vue";
import { useGetAppointments } from "@/composables/api/useAppointmentApi.ts";
import { toEndofDay, toStartOfDay } from "@/util/timeUtil.ts";

const selectedOpen = ref<boolean>(false);
const selectedEvent = ref<CalendarAppointmentEvent | undefined>(undefined);
const selectedElement = ref<HTMLElement | undefined>(undefined);

const { displayedRooms, booking } = defineProps<{
  booking: BookingDetailResponseDTO;
  displayedRooms: RoomListResponseDTO[];
}>();

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

// Transformation of DTOs to vuetify-event-format
const mappedEvents = computed(() => {
  return appointments.value?.content
    ? appointments.value.content.map(
        (appointment) =>
          ({
            start: new Date(appointment.schedule.occupancyStart),
            end: new Date(appointment.schedule.occupancyEnd),
            category: appointment.bookingMinimal.roomId,
            timed: true,
            raw: appointment,
          }) as CalendarAppointmentEvent
      )
    : [];
});

const showEvent = (nativeEvent: Event, payload: { event: unknown }) => {
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
</script>
