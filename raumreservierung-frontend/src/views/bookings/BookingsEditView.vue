<template>
  <base-view header-text="Buchung bearbeiten">
    <template #headerActions>
      <base-button
        class="ml-4"
        :text="t('common.cancel')"
        :prepend-icon="mdiWindowClose"
        secondary
        @click="router.back()"
      />
      <base-button
        class="ml-4"
        :text="isPrivileged ? t('common.deny') : 'Stornieren'"
        :append-icon="mdiCalendarRemoveOutline"
        secondary
      />
      <base-button
        :disabled="!isValid"
        class="ml-4"
        :text="t('common.save')"
        :append-icon="mdiContentSaveOutline"
      />
    </template>
    <template #default>
      <v-form v-model="isValid">
        <v-row>
          <v-col
            cols="12"
            md="8"
          >
            <v-text-field
              v-model="bookingData.title"
              label="Name der Veranstaltung"
            />
          </v-col>
          <v-col
            v-if="isPrivileged"
            cols="12"
            md="4"
          >
            <v-select
              variant="outlined"
              label="Status"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col
            cols="12"
            md="8"
          >
            <room-select
              v-model="bookingData.roomId"
              :loading="getRoomLoading"
              @update:model-value="updateRoom"
            />
          </v-col>
          <v-col
            cols="12"
            md="4"
          >
            <v-number-input
              v-model="bookingData.participantCount"
              label="Teilnehmer"
              variant="outlined"
              suffix="Teilnehmer"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-row> </v-row>
        </v-row>
        <v-row>
          <v-col>
            <equipment-selector
              v-model="bookingData.equipmentIds"
              :filter-ids="currentRoom?.equipmentIds"
              disable-addition
              :loading="getRoomLoading || getBookingLoading"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-textarea
              v-model="bookingData.additionalNotes"
              label="Notizen"
              variant="outlined"
            />
          </v-col>
          <v-col v-if="isPrivileged">
            <v-textarea
              v-model="bookingData.internalNotes"
              label="Interne Notizen"
              variant="outlined"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col
            cols="12"
            :md="bookingId && currentAppointments.length > 1 ? 7 : 12"
          >
            <card-form
              subtitle="Datum und Uhrzeit"
              class="mb-4"
            >
              <template #text>
                <schedule-template-form v-model="bookingData.schedule" />
              </template>
            </card-form>
            <r-rule-editor-card v-model="bookingData.recurringRule" />
          </v-col>

          <v-col
            v-if="bookingId && currentAppointments.length > 1"
            cols="12"
            md="5"
          >
            <appointment-card-list
              v-model="currentAppointments"
              :schedule="bookingData.schedule"
            />
          </v-col>
        </v-row>
      </v-form>
    </template>
  </base-view>
</template>

<script setup lang="ts">
import type {
  AppointmentMinimalResponseDTO,
  BookingRequestDTO,
  RoomRequestDTO,
} from "@/api/raumreservierung-backend";

import {
  mdiCalendarRemoveOutline,
  mdiContentSaveOutline,
  mdiWindowClose,
} from "@mdi/js";
import { computed, onMounted, ref, toRaw } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

import AppointmentCardList from "@/components/booking/AppointmentCardList.vue";
import RRuleEditorCard from "@/components/booking/RRuleEditorCard.vue";
import ScheduleTemplateForm from "@/components/booking/ScheduleTemplateForm.vue";
import BaseView from "@/components/common/BaseView.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CardForm from "@/components/common/CardForm.vue";
import EquipmentSelector from "@/components/rooms/EquipmentSelector.vue";
import RoomSelect from "@/components/rooms/RoomSelect.vue";
import { useGetBooking } from "@/composables/api/useBookingsApi.ts";
import { useRoomCache } from "@/composables/cache/useRoomCache.ts";
import { useIsPrivileged } from "@/composables/useIsPrivileged.ts";
import { ROUTES } from "@/types/Routes.ts";
import {
  EMPTY_BOOKING_REQUEST_DATA,
  mapBookingResponseToRequest,
} from "@/util/bookingTypeUtil.ts";
import { mapResponseToRequest } from "@/util/roomTypeUtil.ts";

const { t } = useI18n();

const route = useRoute();
const router = useRouter();

const isValid = ref<boolean>();
const currentRoom = ref<RoomRequestDTO>();
const currentAppointments = ref<AppointmentMinimalResponseDTO[]>([]);
const bookingData = ref<BookingRequestDTO>(EMPTY_BOOKING_REQUEST_DATA);
const bookingId = computed(() => (route.params.id as string) || undefined);

const isMyBooking = computed(
  () =>
    route.name === ROUTES.MY_BOOKINGS_EDIT ||
    route.name === ROUTES.MY_BOOKINGS_CREATE
);

const isPrivileged = useIsPrivileged([
  "bookings:manage",
  "bookings:write",
  "bookings:read",
]);

const {
  call: getBooking,
  data: getBookingData,
  error: getBookingError,
  loading: getBookingLoading,
} = useGetBooking();

const { call: getRoom, loading: getRoomLoading } = useRoomCache();

onMounted(async () => {
  if (bookingId.value) {
    await getBooking({
      bookingId: bookingId.value,
    });

    if (!getBookingData.value || getBookingError.value) {
      await router.replace({
        name: isMyBooking.value
          ? ROUTES.MY_BOOKINGS_CREATE
          : ROUTES.BOOKINGS_CREATE,
      });
    }

    bookingData.value = mapBookingResponseToRequest(getBookingData.value);
    currentAppointments.value = structuredClone(
      toRaw(getBookingData.value.appointments).slice(0, 7)
    );
    if (getBookingData.value.room?.id) {
      currentRoom.value = mapResponseToRequest(
        await getRoom(getBookingData.value.room?.id)
      );
    }
  } else {
    // reset to clear maybe filled out data away
    bookingData.value = EMPTY_BOOKING_REQUEST_DATA;
  }
});

const updateRoom = async (roomId: string | undefined) => {
  console.log(roomId);
  if (roomId) {
    currentRoom.value = mapResponseToRequest(await getRoom(roomId));
  }
};
</script>

<style scoped></style>
