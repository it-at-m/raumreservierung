<template>
  <base-view :header-text="t('views.bookingDetailsView.header')">
    <template #headerPrepend>
      <v-icon
        size="30"
        :icon="mdiArrowLeft"
        @click="router.back()"
      />
    </template>
    <template #headerActions>
      <base-button
        secondary
        :append-icon="mdiPencil"
        :text="t('common.edit')"
        @click="
          router.push({
            name: isMyBooking ? ROUTES.MY_BOOKINGS_EDIT : ROUTES.BOOKINGS_EDIT,
            params: { id: bookingId },
          })
        "
      />
    </template>
    <template #default>
      <v-sheet
        border
        class="mb-4"
        rounded
        elevation="0"
      >
        <v-row>
          <v-col
            cols="12"
            sm="6"
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
            cols="12"
            sm="6"
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
      </v-sheet>
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
                :subtitle="equipment.description"
              >
              </v-list-item>
            </v-list>
          </details-card>
        </div>
        <div class="masonry-item w-100 d-inline-block mb-4">
          <details-card
            :title="t('domain.seatingType.header')"
            :icon="mdiSofaSingleOutline"
            :subtitle="
              t('views.bookingDetailsView.participants', {
                count: getBookingData?.participantCount,
              })
            "
            :loading="getBookingLoading"
          >
            <v-list>
              <v-list-item
                :title="t('views.bookingDetailsView.chosenSeatingType')"
                :subtitle="t('common.todo')"
              />
            </v-list>
          </details-card>
        </div>
        <div class="masonry-item w-100 d-inline-block mb-4">
          <details-card
            :title="
              t('views.bookingDetailsView.bookedFor', {
                firstName: getBookingData?.bookedFor?.firstName,
                lastName: getBookingData?.bookedFor?.lastName,
              })
            "
            :subtitle="bookedByComputed"
            :icon="mdiAccountOutline"
            :loading="getBookingLoading"
          >
            <div
              v-if="getBookingData?.organisationUnit"
              class="text-medium-emphasis d-flex align-center"
            >
              <v-icon
                :icon="mdiDomain"
                size="small"
                class="mr-2"
              />
              {{ getBookingData.organisationUnit }}
            </div>
          </details-card>
        </div>
        <div
          v-if="getBookingData?.recurringRule"
          class="masonry-item w-100 d-inline-block mb-4"
        >
          <details-card
            :title="t('domain.booking.recurrenceRule')"
            :subtitle="computedRRule"
            :icon="mdiCalendarSyncOutline"
            :loading="getBookingLoading"
          />
        </div>
        <div
          v-if="visibleAppointments.length > 1"
          class="masonry-item w-100 d-inline-block mb-4"
        >
          <details-card
            :title="t('views.bookingDetailsView.seriesDate')"
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
            :title="t('views.bookingDetailsView.notes')"
            :icon="mdiFileDocumentOutline"
            :loading="getBookingLoading"
          >
            <v-list class="py-0">
              <v-list-item class="pre-line">
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
            :title="t('views.bookingDetailsView.internalNotes')"
            :icon="mdiFileDocumentAlertOutline"
            :loading="getBookingLoading"
          >
            <v-list class="py-0">
              <v-list-item class="pre-line">
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
  mdiArrowLeft,
  mdiCalendarRangeOutline,
  mdiCalendarSyncOutline,
  mdiDomain,
  mdiDoor,
  mdiFileDocumentAlertOutline,
  mdiFileDocumentOutline,
  mdiPencil,
  mdiSofaSingleOutline,
} from "@mdi/js";
import { RRule } from "rrule";
import { computed, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { useDisplay } from "vuetify/framework";

import AppointmentListItem from "@/components/booking/AppointmentListItem.vue";
import BookingGeneralInfoCard from "@/components/booking/BookingGeneralInfoCard.vue";
import ScheduleTimelineCard from "@/components/booking/ScheduleTimelineCard.vue";
import BaseView from "@/components/common/BaseView.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import DetailsCard from "@/components/common/DetailsCard.vue";
import { useGetBooking } from "@/composables/api/useBookingsApi.ts";
import { rruleDeLanguage, rruleGetText } from "@/plugins/i18n.ts";
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
    : t("views.bookingDetailsView.bookedBy", {
        firstName: getBookingData?.value?.bookedBy?.firstName,
        lastName: getBookingData?.value?.bookedBy?.lastName,
      })
);

const computedRRule = computed(() => {
  if (getBookingData?.value?.recurringRule) {
    const rrule = RRule.fromString(getBookingData.value.recurringRule);
    return rrule.toText(rruleGetText, rruleDeLanguage);
  }
  return "";
});
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

.pre-line {
  white-space: pre-line;
}
</style>
