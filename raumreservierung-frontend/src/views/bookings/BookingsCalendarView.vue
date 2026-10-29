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
        ref="bookingCalendar"
        :booking-id="getBookingData?.id"
        :room-id="booking?.roomId"
        :focus-date="
          booking?.schedule.occupancyStart ??
          getBookingData?.schedule.occupancyStart ??
          new Date()
        "
        :displayed-rooms="selectedRoomDataRef.selectedRoomData"
        @updated-schedule="updateSchedule"
      />
      <v-alert
        v-if="isSeriesBooking"
        border="start"
        color="warning"
        variant="tonal"
        class="mt-4"
      >
        {{ t("components.bookingsCalendarView.warning") }}
      </v-alert>
      <booking-details-summary
        v-if="bookingId"
        class="mt-4"
        :booking="computedBooking"
        :booking-changed="eventChanged"
        :loading="getBookingLoading || updateBookingLoading"
        @status-action="updateBookingStatus"
        @cancel="resetBooking"
        @confirm="handleConfirm"
      />
      <v-card
        v-else
        class="mt-4"
      >
        <template #actions>
          <i18n-t
            v-if="
              booking &&
              dateEquals(
                booking.schedule.occupancyStart,
                booking.schedule.occupancyEnd
              )
            "
            class="ml-1"
            keypath="views.bookingsCalendarView.createInDateRangeSameDay"
            scope="global"
            tag="span"
          >
            <template #date>
              {{ d(booking?.schedule.occupancyStart, "short") }}
            </template>
            <template #start>
              {{ d(booking?.schedule.occupancyStart, "time") }}
            </template>
            <template #end>
              {{ d(booking?.schedule.occupancyEnd, "time") }}
            </template>
          </i18n-t>
          <i18n-t
            v-else-if="booking"
            class="ml-1"
            keypath="views.bookingsCalendarView.createInDateRangeMultiDay"
            scope="global"
            tag="span"
          >
            <template #start>
              {{ d(booking?.schedule.occupancyStart, "long") }}
            </template>
            <template #end>
              {{ d(booking?.schedule.occupancyEnd, "long") }}
            </template>
          </i18n-t>
          <i18n-t
            v-else
            class="ml-1"
            keypath="views.bookingsCalendarView.drawNewDateRange"
            scope="global"
            tag="span"
          />
          <v-spacer />
          <base-button
            text="Erstellen"
            :disabled="!booking"
            :append-icon="mdiCalendarQuestion"
            @click="
              router.push({
                name: ROUTES.MY_BOOKINGS_CREATE,
                query: {
                  roomId: booking?.roomId,
                  start: booking?.schedule.occupancyStart.toISOString(),
                  end: booking?.schedule.occupancyEnd.toISOString(),
                },
              })
            "
          />
        </template>
      </v-card>
    </template>
  </base-view>
</template>

<script setup lang="ts">
import type {
  BookingDetailResponseDTO,
  BookingRequestDTO,
  RoomListResponseDTO,
  ScheduleTemplate,
} from "@/api/raumreservierung-backend";

import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend";
import type { CalendarAppointmentEvent } from "@/components/booking/calendar/rrCalendarAppointmentEvent.vue";
import type { ComponentPublicInstance } from "vue";

import { mdiArrowLeft, mdiCalendarQuestion } from "@mdi/js";
import { computed, ref, useTemplateRef, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

import BookingDetailsSummary from "@/components/booking/BookingDetailsSummary.vue";
import RrBookingCalendar from "@/components/booking/calendar/rrBookingCalendar.vue";
import BaseView from "@/components/common/BaseView.vue";
import RoomSelect from "@/components/rooms/RoomSelect.vue";
import {
  useGetBooking,
  useUpdateBooking,
} from "@/composables/api/useBookingsApi.ts";
import { useIsBookingEditable } from "@/composables/useBookingStatus.ts";
import { useRules } from "@/composables/useRules.ts";
import { STATUS_INDICATORS } from "@/constants.ts";
import { ROUTES } from "@/types/Routes.ts";
import { mapBookingResponseToRequest } from "@/util/bookingTypeUtil.ts";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import { dateEquals } from "@/util/timeUtil.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

interface RoomSelectExposed extends ComponentPublicInstance {
  selectedRoomData: RoomListResponseDTO[];
}

interface RrBookingCalendarExposed extends ComponentPublicInstance {
  resetAppointments: () => unknown;
}

const { t, d } = useI18n();
const route = useRoute();
const router = useRouter();

const rules = useRules();
const isBookingEditable = useIsBookingEditable();

const booking = ref<BookingRequestDTO>();
const saveHintOpen = ref(false);

const bookingId = computed(() => (route.params.id as string) || undefined);
const selectedRoomDataRef = useTemplateRef<RoomSelectExposed>("roomSelect");
const bookingCalendarRef =
  useTemplateRef<RrBookingCalendarExposed>("bookingCalendar");

const manualRoomIds = ref<string[] | null>(null);

const { data: getBookingData, isLoading: getBookingLoading } = useGetBooking(
  bookingId.value
);

const {
  call: updateBooking,
  loading: updateBookingLoading,
  error: updateBookingError,
} = useUpdateBooking();

const snackbarStore = useSnackbarStore();

watch(
  getBookingData,
  () => {
    if (getBookingData.value) {
      if (!isBookingEditable(getBookingData.value)) {
        router.push({
          name: ROUTES.BOOKINGS_LIST,
        });
      }
      booking.value = mapBookingResponseToRequest(getBookingData.value);
    }
  },
  { immediate: true }
);

const isSeriesBooking = computed(() => !!getBookingData.value?.recurringRule);

const computedBooking = computed(() => {
  if (!booking.value && !getBookingData.value) {
    return undefined;
  }
  return {
    ...getBookingData.value,
    schedule: booking.value?.schedule,
  } as BookingDetailResponseDTO;
});

const eventChanged = computed(
  () =>
    getBookingData.value &&
    booking.value &&
    (getBookingData.value.schedule.occupancyStart.getTime() !==
      booking.value.schedule.occupancyStart.getTime() ||
      (getBookingData.value.room &&
        getBookingData.value.room.id !== booking.value.roomId))
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

const handleConfirm = async () => {
  if (isSeriesBooking.value) {
    saveHintOpen.value = true;
    return;
  }
  await saveBooking();
};

const saveBooking = async () => {
  await updateBookingSchedule();
  if (!updateBookingError) {
    snackbarStore.push({
      text: t("generics.updated", { domain: t("domain.booking.header") }),
      color: STATUS_INDICATORS.SUCCESS,
    });
  }
};

/**
 * Updates {@link booking} by calculating the diff from new start and end Date and applying it to the schedule and updating the roomId.
 */
const updateSchedule = (event: CalendarAppointmentEvent) => {
  const diffMs = booking.value
    ? event.start.getTime() - booking.value.schedule.occupancyStart.getTime()
    : 0;

  const newSchedule: ScheduleTemplate = {
    occupancyStart: event.start,
    occupancyEnd: event.end,
    appointmentStart: booking.value?.schedule.appointmentStart
      ? new Date(booking.value.schedule.appointmentStart.getTime() + diffMs)
      : undefined,
    appointmentEnd: booking.value?.schedule.appointmentEnd
      ? new Date(booking.value.schedule.appointmentEnd.getTime() + diffMs)
      : undefined,
  };
  booking.value = {
    ...booking.value,
    title: booking.value?.title ?? "",
    status: booking.value?.status ?? BookingStatusDTOCurrentStatusEnum.NEW,
    schedule: newSchedule,
    roomId: event.category || manualRoomIds.value?.[0] || "",
  };
};

/**
 * Updates the status of the current booking and ignores the other fields
 * @param newStatus to be saved
 */
const updateBookingStatus = async (
  newStatus: BookingStatusDTOCurrentStatusEnum
) => {
  if (getBookingData.value) {
    const bookingRequest = mapBookingResponseToRequest(getBookingData.value);

    await updateBooking({
      bookingId: getBookingData.value.id,
      bookingRequestDTO: { ...bookingRequest, status: newStatus },
    });

    await router.push({
      name: ROUTES.BOOKINGS_LIST,
    });
  }
};

/**
 * Updates the changed schedule or room
 */
const updateBookingSchedule = async () => {
  if (booking.value && getBookingData.value) {
    await updateBooking({
      bookingId: getBookingData.value.id,
      bookingRequestDTO: booking.value,
    });
  }
  if (getBookingData.value && bookingCalendarRef.value) {
    bookingCalendarRef.value.resetAppointments();
  }
};

const resetBooking = () => {
  if (getBookingData.value && bookingCalendarRef.value) {
    booking.value = mapBookingResponseToRequest(getBookingData.value);
    bookingCalendarRef.value.resetAppointments();
  }
};
</script>

<style scoped></style>
