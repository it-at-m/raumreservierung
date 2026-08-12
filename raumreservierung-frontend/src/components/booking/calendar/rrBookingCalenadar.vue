<template>
  <v-calendar
    :type="isDayView ? 'category' : 'custom-daily'"
    :start="startDate"
    :end="isDayView ? undefined : endDate"
    :categories="calendarCategories"
    :interval-minutes="120"
    :interval-height="80"
    :first-interval="6"
  >
  </v-calendar>
</template>

<script setup lang="ts">
import type { RoomListResponseDTO } from "@/api/raumreservierung-backend";

import { computed } from "vue";

const { displayedRooms } = defineProps<{
  displayedRooms: RoomListResponseDTO[];
}>();

const calendarCategories = computed(() =>
  displayedRooms.map((room) => room.name)
);

const isDayView = computed(() => displayedRooms.length > 1);
const startDate = computed(() => new Date());

// Explizites Enddatum zwingt den Kalender in die 10-Tage-Spanne
const endDate = computed(() => {
  const end = new Date(startDate.value);
  end.setDate(end.getDate() + 9);
  return end;
});

// Transformation der DTOs in das von Vuetify erwartete Event-Format
// const mappedEvents = computed(() => {
//   return appointments.map((appointment) => ({
//     title: appointment.title, // Mapping anpassen auf tatsächliche DTO-Felder
//     start: new Date(appointment.startTime),
//     end: new Date(appointment.endTime),
//     // category ist zwingend für type="category", muss mit room.name übereinstimmen
//     category: appointment.roomName,
//     color: "primary", // Vuetify Standardfarbe
//   }));
// });
</script>
