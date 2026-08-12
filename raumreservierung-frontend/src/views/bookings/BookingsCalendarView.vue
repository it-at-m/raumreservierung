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
        :model-value="selectedRoom"
        class="mb-4"
        :label="
          t('generics.select', {
            domain: t('domain.room.header', { count: 2 }),
          })
        "
        multiple
        :rules="[rules.required('Bitte wählen Sie mindestens einen Raum')]"
        @update:room-data="selectedRoomData = $event"
      />
      <rr-booking-calenadar
        v-if="selectedRoomData.length > 0"
        :displayed-rooms="selectedRoomData"
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

import { mdiArrowLeft } from "@mdi/js";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

import BookingDetailsSummary from "@/components/booking/BookingDetailsSummary.vue";
import RrBookingCalenadar from "@/components/booking/calendar/rrBookingCalenadar.vue";
import BaseView from "@/components/common/BaseView.vue";
import RoomSelect from "@/components/rooms/RoomSelect.vue";
import { useGetBookingTS } from "@/composables/api/useBookingsApi.ts";
import { useRules } from "@/composables/useRules.ts";

const { t } = useI18n();
const route = useRoute();
const router = useRouter();
const rules = useRules();

const bookingId = computed(() => (route.params.id as string) || undefined);

const selectedRoomData = ref<RoomListResponseDTO[]>([]);

//TODO Does not work full as intended! Setting v-model does not inturn always trigger recomputing of selectedRoomData
const selectedRoom = computed(() =>
  selectedRoomData.value.length === 0
    ? [getBookingData?.value?.room?.id || ""]
    : selectedRoomData.value
        .filter((roomData) => roomData.id !== undefined)
        .map((roomData) => roomData.id ?? "")
);

const { data: getBookingData, isLoading: getBookingLoading } = useGetBookingTS(
  bookingId.value
);
</script>

<style scoped></style>
