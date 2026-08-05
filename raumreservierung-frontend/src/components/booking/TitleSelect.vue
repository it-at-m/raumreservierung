<template>
  <v-autocomplete
    v-model="modelValue"
    label="Veranstaltungstitel"
    color="accent"
    variant="outlined"
    density="compact"
    clearable
    :prepend-inner-icon="mdiTextBoxSearchOutline"
    :items="bookings?.content ?? []"
    :loading="bookingsLoading"
    :item-title="formatTitle"
    item-value="title"
    hide-no-data
    @update:search="onSearch"
  >
    <template #selection="{ item }">
      <span class="text-body-1">{{ formatTitle(item) }}</span>
    </template>
  </v-autocomplete>
</template>
<script setup lang="ts">
import type { BookingListResponseDTO } from "@/api/raumreservierung-backend";

import { mdiTextBoxSearchOutline } from "@mdi/js";
import { useDebounceFn } from "@vueuse/core";

import { useGetBookings } from "@/composables/api/useBookingsApi.ts";

const modelValue = defineModel<string>();

const {
  call: getBookings,
  data: bookings,
  loading: bookingsLoading,
} = useGetBookings();

const formatTitle = (booking: BookingListResponseDTO) => {
  if (!booking) {
    return "";
  }

  return `${booking.title || ""}`.trim();
};

const onSearch = useDebounceFn((searchQuery: string) => {
  if (!searchQuery) {
    return;
  }

  getBookings({
    title: searchQuery,
    page: 0,
    size: 10,
    self: false,
  });
}, 500);
</script>

<style scoped></style>
