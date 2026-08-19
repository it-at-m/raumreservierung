<template>
  <base-view
    :header-text="t('generics.manage', { domain: t('domain.booking.header') })"
  >
    <template #headerPrepend>
      <v-icon
        size="30"
        :icon="mdiArrowLeft"
        @click="router.back()"
      />
    </template>
    <template #default>
      <room-select
        ref="roomSelect"
        v-model="selectedRoomIds"
        class="mb-4"
        :label="
          t('generics.select', {
            domain: t('domain.room.header', { count: 2 }),
          })
        "
        multiple
        :rules="[rules.required('Bitte wählen Sie mindestens einen Raum')]"
      />
      <rr-booking-calendar
        v-if="
          selectedRoomDataRef?.selectedRoomData &&
          selectedRoomDataRef.selectedRoomData.length > 0
        "
        :displayed-rooms="selectedRoomDataRef.selectedRoomData"
      />
      <booking-details-summary
        class="mt-4"
        :booking="getBookingData"
        :loading="getBookingLoading"
      />
    </template>
  </base-view>
</template>

<script setup lang="ts">
import type { RoomListResponseDTO } from "@/api/raumreservierung-backend";
import type { ComponentPublicInstance } from "vue";

import { mdiArrowLeft } from "@mdi/js";
import { computed, ref, useTemplateRef } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

import BookingDetailsSummary from "@/components/booking/BookingDetailsSummary.vue";
import RrBookingCalendar from "@/components/booking/calendar/rrBookingCalenadar.vue";
import BaseView from "@/components/common/BaseView.vue";
import RoomSelect from "@/components/rooms/RoomSelect.vue";
import { useGetBookingTS } from "@/composables/api/useBookingsApi.ts";
import { useRules } from "@/composables/useRules.ts";

interface RoomSelectExposed extends ComponentPublicInstance {
  selectedRoomData: RoomListResponseDTO[];
}
const { t } = useI18n();
const route = useRoute();
const router = useRouter();

const rules = useRules();

const bookingId = computed(() => (route.params.id as string) || undefined);
const selectedRoomDataRef = useTemplateRef<RoomSelectExposed>("roomSelect");

const manualRoomIds = ref<string[] | null>(null);

const { data: getBookingData, isLoading: getBookingLoading } = useGetBookingTS(
  bookingId.value
);

const selectedRoomIds = computed({
  get() {
    if (manualRoomIds.value !== null) {
      return manualRoomIds.value;
    }
    const apiRoomId = getBookingData.value?.room?.id;
    return apiRoomId ? [apiRoomId] : [];
  },
  set(newValue) {
    manualRoomIds.value = newValue;
  },
});
</script>

<style scoped></style>
