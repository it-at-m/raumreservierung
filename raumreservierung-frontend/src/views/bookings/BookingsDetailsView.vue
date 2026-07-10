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
                  :booking-type="getBookingData?.bookingType"
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
            :title="
              getBookingData?.room?.name ||
              t('views.bookingDetailsView.noRoomSelected')
            "
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
          v-if="(appointmentPage?.page?.totalElements || 0) > 1"
          class="masonry-item w-100 d-inline-block mb-4"
        >
          <details-card
            :title="t('views.bookingDetailsView.seriesDate')"
            :icon="mdiCalendarRangeOutline"
            :loading="getBookingLoading"
          >
            <v-infinite-scroll
              class="text-medium-emphasis"
              :items="appointments"
              mode="manual"
              max-height="450px"
              @load="loadAppointmentPage"
            >
              <template #default>
                <appointment-list-item
                  v-for="appointment in appointments"
                  :key="appointment.id"
                  class="mb-2"
                  :appointment="appointment"
                  :schedule="getBookingData?.schedule"
                />
              </template>
              <template #load-more="{ props }">
                <base-button
                  v-if="
                    (appointmentPage.page?.totalPages || 0) >
                    nextAppointmentPage
                  "
                  secondary
                  text="Weitere Termine laden"
                  v-bind="props"
                />
              </template>
            </v-infinite-scroll>
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
import type { AppointmentDetailsResponseDTO } from "@/api/raumreservierung-backend";
import type { InfiniteScrollLoad } from "@/types/InfiniteScroll.ts";

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
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { useDisplay } from "vuetify/framework";

import AppointmentListItem from "@/components/booking/AppointmentListItem.vue";
import BookingGeneralInfoCard from "@/components/booking/BookingGeneralInfoCard.vue";
import ScheduleTimelineCard from "@/components/booking/ScheduleTimelineCard.vue";
import BaseView from "@/components/common/BaseView.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import DetailsCard from "@/components/common/DetailsCard.vue";
import { useGetAppointments } from "@/composables/api/useAppointmentApi.ts";
import { useGetBooking } from "@/composables/api/useBookingsApi.ts";
import { rruleDeLanguage, rruleGetText } from "@/plugins/i18n.ts";
import { ROUTES } from "@/types/Routes.ts";

const { t } = useI18n();

const route = useRoute();
const router = useRouter();
const { mdAndUp } = useDisplay();

const isMyBooking = computed(() => route.name === ROUTES.MY_BOOKINGS_DETAILS);

const bookingId = computed(() => (route.params.id as string) || undefined);
const appointments = ref<AppointmentDetailsResponseDTO[]>([]);
const nextAppointmentPage = ref<number>(0);
const totalPages = ref<number>(1);

const {
  call: getBooking,
  data: getBookingData,
  error: getBookingError,
  loading: getBookingLoading,
} = useGetBooking();

const { call: getAppointmentPage, data: appointmentPage } =
  useGetAppointments();

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

    nextAppointmentPage.value = 0;
    await fetchPage();

    if (appointmentPage.value?.content) {
      appointments.value = [...appointmentPage.value.content];
      totalPages.value = appointmentPage.value.page?.totalPages || 1;
      nextAppointmentPage.value++;
    }
  }
});

const fetchPage = async () => {
  if (!bookingId.value) {
    return;
  }

  await getAppointmentPage({
    page: nextAppointmentPage.value,
    startDate: new Date(),
    bookingId: bookingId.value,
    endDate: new Date(new Date().setFullYear(new Date().getFullYear() + 1)),
    size: 5,
  });
};

const loadAppointmentPage = async (event: InfiniteScrollLoad) => {
  const { done } = event;

  if (!bookingId.value || nextAppointmentPage.value >= totalPages.value) {
    done("empty");
    return;
  }

  await fetchPage();

  if (appointmentPage.value?.content) {
    appointments.value.push(...appointmentPage.value.content);
    nextAppointmentPage.value++;
    done("ok");
  } else {
    done("error");
  }
};

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
