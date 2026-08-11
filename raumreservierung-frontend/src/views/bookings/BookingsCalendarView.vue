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
      <booking-details-summary
        :booking="getBookingData"
        :loading="getBookingLoading"
      />
    </template>
  </base-view>
</template>

<script setup lang="ts">
import { mdiArrowLeft } from "@mdi/js";
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

import BookingDetailsSummary from "@/components/booking/BookingDetailsSummary.vue";
import BaseView from "@/components/common/BaseView.vue";
import { useGetBookingTS } from "@/composables/api/useBookingsApi.ts";

const { t } = useI18n();
const route = useRoute();
const router = useRouter();

const bookingId = computed(() => (route.params.id as string) || undefined);

const { data: getBookingData, isLoading: getBookingLoading } = useGetBookingTS(
  bookingId.value
);
</script>

<style scoped></style>
