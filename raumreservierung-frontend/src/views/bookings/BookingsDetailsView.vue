<template>
  <base-view :header-text="t('views.bookingDetailsView.header')">
    <template #default>
      <v-row class="mb-4">
        <v-col
          flat
          cols="12"
          md="6"
        >
          <v-skeleton-loader
            :loading="getBookingLoading"
            type="article"
          >
            <v-responsive>
              <booking-general-info-card
                :id="getBookingData?.id"
                :title="getBookingData?.title"
                :schedule="getBookingData?.schedule"
              />
            </v-responsive>
          </v-skeleton-loader>
        </v-col>
        <v-col
          flat
          cols="12"
          md="6"
        >
          <v-skeleton-loader
            :loading="getBookingLoading"
            type="article"
          >
            <v-responsive>
              <schedule-timeline-card
                v-if="getBookingData?.schedule"
                :schedule="getBookingData?.schedule"
              />
            </v-responsive>
          </v-skeleton-loader>
        </v-col>
      </v-row>
      <div
        class="w-100 masonry-container"
        :class="mdAndUp ? 'masonry-cols-2' : 'masonry-cols-1'"
      >
        <div class="masonry-item w-100 d-inline-block mb-4">
          <details-card
            :title="getBookingData?.room?.name || ''"
            :subtitle="getBookingData?.room?.number"
            :icon="mdiDoor"
            :loading="getBookingLoading"
          >
            <v-list density="compact">
              <v-list-item
                v-for="equipment in getBookingData?.equipments"
                :key="equipment.id"
                :title="equipment.name"
              >
              </v-list-item>
            </v-list>
          </details-card>
        </div>
        <div class="masonry-item w-100 d-inline-block mb-4">
          <details-card
            title="Bestuhlung"
            :icon="mdiSofaSingleOutline"
            :subtitle="'Teilnehmer ' + getBookingData?.participantCount"
            :loading="getBookingLoading"
          >
            <v-list>
              <v-list-item
                title="Gewählte Bestuhlung"
                subtitle="HIER STEHT DIE GEWÄHLTE BESTUHLUNG"
              />
            </v-list>
          </details-card>
        </div>
        <div class="masonry-item w-100 d-inline-block mb-4">
          <details-card
            :title="`Gebucht für ${getBookingData?.bookedFor?.firstName} ${getBookingData?.bookedFor?.lastName}`"
            :subtitle="bookedByComputed"
            :icon="mdiAccountOutline"
            :loading="getBookingLoading"
          />
        </div>
        <div
          v-if="visibleAppointments.length > 1"
          class="masonry-item w-100 d-inline-block mb-4"
        >
          <details-card
            title="Serientermine"
            :icon="mdiCalendarRangeOutline"
            :loading="getBookingLoading"
          >
            <v-list>
              <appointment-list-item
                v-for="appointment in visibleAppointments"
                :key="appointment.id"
                class="mb-2"
                :appointment="appointment"
                :schedule="getBookingData?.schedule"
              />
            </v-list>
          </details-card>
        </div>
        <div
          v-if="getBookingData?.additionalNotes"
          class="masonry-item w-100 d-inline-block mb-4"
        >
          <details-card
            title="Notizen"
            :icon="mdiFileDocumentOutline"
            :loading="getBookingLoading"
          >
            <v-list class="py-0">
              <v-list-item>
                {{ getBookingData?.additionalNotes }}
              </v-list-item>
            </v-list>
            <p></p>
          </details-card>
        </div>
        <div
          v-if="getBookingData?.internalNotes"
          class="masonry-item w-100 d-inline-block mb-4"
        >
          <details-card
            title="Interne Notizen"
            :icon="mdiFileDocumentAlertOutline"
            :loading="getBookingLoading"
          >
            <v-list class="py-0">
              <v-list-item>
                {{ getBookingData?.internalNotes }}
              </v-list-item>
            </v-list>
          </details-card>
        </div>
      </div>
    </template>
  </base-view>
</template>

<script setup lang="ts">
import {
  mdiAccountOutline,
  mdiCalendarRangeOutline,
  mdiDoor,
  mdiFileDocumentAlertOutline,
  mdiFileDocumentOutline,
  mdiSofaSingleOutline,
} from "@mdi/js";
import { computed, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { useDisplay } from "vuetify/framework";

import AppointmentListItem from "@/components/booking/AppointmentListItem.vue";
import BookingGeneralInfoCard from "@/components/booking/BookingGeneralInfoCard.vue";
import ScheduleTimelineCard from "@/components/booking/ScheduleTimelineCard.vue";
import BaseView from "@/components/common/BaseView.vue";
import DetailsCard from "@/components/common/DetailsCard.vue";
import { useGetBooking } from "@/composables/api/useBookingsApi.ts";
import { ROUTES } from "@/types/Routes.ts";

const { t } = useI18n();

const route = useRoute();
const router = useRouter();
const { mdAndUp } = useDisplay();

const isMyBooking = computed(() => route.name === ROUTES.MY_BOOKINGS_DETAILS);

const bookingId = computed(() => (route.params.id as string) || undefined);

const {
  call: getBooking,
  data: getBookingData,
  error: getBookingError,
  loading: getBookingLoading,
} = useGetBooking();

onMounted(async () => {
  if (bookingId.value) {
    await getBooking({
      bookingId: bookingId.value,
    });

    if (!getBookingData.value || getBookingError.value) {
      await router.replace({
        name: isMyBooking.value
          ? ROUTES.MY_BOOKINGS_LIST
          : ROUTES.BOOKINGS_LIST,
      });
    }
  }
});

const visibleAppointments = computed(() => {
  if (!getBookingData.value) {
    return [];
  }
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const firstVisibleIndex = getBookingData?.value.appointments.findIndex(
    (app) => new Date(app.schedule.occupancyStart) >= today
  );

  // TODO: getFirstFuture Appointment
  return getBookingData?.value.appointments.slice(
    firstVisibleIndex,
    firstVisibleIndex + 5
  );
});

const bookedByComputed = computed(() =>
  getBookingData?.value?.bookedBy?.id === getBookingData?.value?.bookedFor?.id
    ? ""
    : `Gebucht von ${getBookingData?.value?.bookedBy?.firstName} ${getBookingData?.value?.bookedBy?.lastName}`
);
</script>

<style scoped>
.masonry-container {
  column-gap: 16px;
}

.masonry-cols-1 {
  column-count: 1;
}

.masonry-cols-2 {
  column-count: 2;
}

.masonry-item {
  break-inside: avoid-column;
}
</style>
