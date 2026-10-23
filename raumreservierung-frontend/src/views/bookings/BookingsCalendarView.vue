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
        show-inactive
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
          getBookingData &&
          selectedRoomDataRef?.selectedRoomData &&
          selectedRoomDataRef.selectedRoomData.length > 0
        "
        ref="bookingCalendar"
        :booking-id="getBookingData.id"
        :room-id="booking?.roomId"
        :focus-date="
          booking?.schedule.occupancyStart ??
          getBookingData.schedule.occupancyStart
        "
        :displayed-rooms="selectedRoomDataRef.selectedRoomData"
        @updated-schedule="updateSchedule"
      />
      <booking-details-summary
        class="mt-4"
        :booking="computedBooking"
        :booking-changed="eventChanged"
        :loading="getBookingLoading || updateBookingLoading"
        @status-action="updateBookingStatus"
        @cancel="resetBooking"
        @confirm="handleConfirm"
      />
      <v-dialog
        v-if="isSeriesBooking"
        v-model="saveHintOpen"
        max-width="480"
      >
        <confirm-card
          :title="t('components.bookingsCalendarView.saveHint.title')"
          :text="t('components.bookingsCalendarView.saveHint.booking')"
          :loading="updateBookingLoading"
          @cancel="saveHintOpen = false"
          @confirm="confirmSave"
        >
          <template #confirm="{ props, disabled }">
            <base-button
              v-bind="props"
              :disabled="disabled"
              :text="t('common.save')"
            />
          </template>
        </confirm-card>
      </v-dialog>
    </template>
  </base-view>
</template>

<script setup lang="ts">
import type {
  BookingDetailResponseDTO,
  BookingRequestDTO,
  BookingStatusDTOCurrentStatusEnum,
  RoomListResponseDTO,
  ScheduleTemplate,
} from "@/api/raumreservierung-backend";
import type { CalendarAppointmentEvent } from "@/components/booking/calendar/rrCalendarAppointmentEvent.vue";
import type { ComponentPublicInstance } from "vue";
import { computed, ref, useTemplateRef, watch } from "vue";

import { mdiArrowLeft } from "@mdi/js";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

import BookingDetailsSummary from "@/components/booking/BookingDetailsSummary.vue";
import RrBookingCalendar from "@/components/booking/calendar/rrBookingCalendar.vue";
import BaseView from "@/components/common/BaseView.vue";
import RoomSelect from "@/components/rooms/RoomSelect.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";
import {
  useGetBooking,
  useUpdateBooking,
} from "@/composables/api/useBookingsApi.ts";
import { useIsBookingEditable } from "@/composables/useBookingStatus.ts";
import { useRules } from "@/composables/useRules.ts";
import { FALLBACK_CATEGORY_ROOM, STATUS_INDICATORS } from "@/constants.ts";
import { ROUTES } from "@/types/Routes.ts";
import { mapBookingResponseToRequest } from "@/util/bookingTypeUtil.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

interface RoomSelectExposed extends ComponentPublicInstance {
  selectedRoomData: RoomListResponseDTO[];
}

interface RrBookingCalendarExposed extends ComponentPublicInstance {
  resetAppointments: () => unknown;
}

const { t } = useI18n();
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

const confirmSave = async () => {
  saveHintOpen.value = false;
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
  if (!booking.value) {
    return;
  }
  const diffMs =
    event.start.getTime() - booking.value.schedule.occupancyStart.getTime();

  const newSchedule: ScheduleTemplate = {
    occupancyStart: event.start,
    occupancyEnd: event.end,
    appointmentStart: booking.value.schedule.appointmentStart
      ? new Date(booking.value.schedule.appointmentStart.getTime() + diffMs)
      : undefined,
    appointmentEnd: booking.value.schedule.appointmentEnd
      ? new Date(booking.value.schedule.appointmentEnd.getTime() + diffMs)
      : undefined,
  };

  booking.value = {
    ...booking.value,
    schedule: newSchedule,
    roomId:
      event.category === FALLBACK_CATEGORY_ROOM.categoryName
        ? undefined
        : event.category,
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
