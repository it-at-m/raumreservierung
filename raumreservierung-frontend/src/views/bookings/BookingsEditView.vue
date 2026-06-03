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
        :text="isPrivileged ? t('common.deny') : t('common.rescind')"
        :append-icon="mdiCalendarRemoveOutline"
        secondary
      />
      <base-button
        :disabled="!isValid"
        class="ml-4"
        :text="t('common.save')"
        :append-icon="mdiContentSaveOutline"
        @click="saveBooking"
      />
    </template>
    <template #default>
      <v-form
        v-model="isValid"
        :disabled="createBookingLoading || updateBookingLoading"
      >
        <v-row>
          <v-col
            cols="12"
            :md="isPrivileged ? 8 : 12"
          >
            <v-text-field
              v-model="bookingData.title"
              :rules="[
                rules.required(
                  t('common.rules.notEmpty', {
                    field: t('domain.booking.title'),
                  })
                ),
              ]"
              label="Name der Veranstaltung"
              hide-details="auto"
            />
          </v-col>
          <v-col
            v-if="isPrivileged"
            cols="12"
            md="4"
          >
            <v-select
              disabled
              variant="outlined"
              label="Status"
              hide-details
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <room-select
              v-model="bookingData.roomId"
              label="Raumauswahl"
              :loading="
                getRoomLoading || createBookingLoading || updateBookingLoading
              "
              @update:model-value="updateRoom"
            />
          </v-col>
        </v-row>
        <v-row v-if="currentRoom">
          <v-row>
            <v-col
              cols="12"
              md="8"
            >
              <seating-type-selector
                v-model="bookingData.seatingTypeId"
                :allowed-ids="currentRoomSeatingTypeIds"
                :info-max-capacity="currentRoomSeatingTypeLimit"
                :loading="createBookingLoading || updateBookingLoading"
              />
            </v-col>
            <v-col
              cols="12"
              md="4"
            >
              <v-number-input
                v-model="bookingData.participantCount"
                :label="t('views.bookingEditView.participantsCount')"
                variant="outlined"
                :min="1"
                :max="currentRoomSeatingTypeLimit"
                :suffix="
                  t('domain.booking.participants', {
                    num: 1,
                  })
                "
              />
            </v-col>
          </v-row>
        </v-row>
        <v-row>
          <v-col>
            <equipment-selector
              v-model="bookingData.equipmentIds"
              :filter-ids="currentRoom?.equipmentIds"
              disable-addition
              :loading="
                getRoomLoading ||
                getBookingLoading ||
                createBookingLoading ||
                updateBookingLoading
              "
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <card-form>
              <template #text>
                <v-checkbox
                  v-model="bookingData.cateringNeeded"
                  color="accent"
                  density="compact"
                  hide-details
                  label="Catering geplant"
                />
              </template>
            </card-form>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <card-form subtitle="Buchung für andere Person">
              <template #text>
                <v-row>
                  <v-col>
                    <v-select
                      :model-value="bookedFor?.id"
                      color=" accent"
                      variant="outlined"
                      :prepend-inner-icon="mdiAccountSearchOutline"
                      disabled
                      label="Interne Person"
                    />
                  </v-col>
                  <v-col>
                    <external-person-select v-model="bookedFor" />
                  </v-col>
                </v-row>
              </template>
            </card-form>
          </v-col>
        </v-row>
        <v-row>
          <v-col
            cols="12"
            :xl="bookingId && currentAppointments.length > 1 ? 7 : 12"
          >
            <card-form
              subtitle="Datum und Uhrzeit"
              :class="{ 'mb-4': isSeriesBooking }"
            >
              <template #text>
                <schedule-template-form
                  v-model="bookingData.schedule"
                  :disabled="createBookingLoading || updateBookingLoading"
                >
                  <template #checks>
                    <v-checkbox
                      v-model="isSeriesBooking"
                      color="accent"
                      label="Serientermin"
                      hide-details
                      density="compact"
                      @update:model-value="updateRRule"
                    />
                  </template>
                </schedule-template-form>
              </template>
            </card-form>
            <r-rule-editor-card
              v-if="isSeriesBooking"
              v-model="bookingData.recurringRule"
              :disabled="createBookingLoading || updateBookingLoading"
            />
          </v-col>
          <v-col
            v-if="bookingId && currentAppointments.length > 1"
            cols="12"
            xl="5"
          >
            <appointment-card-list
              v-model="currentAppointments"
              :schedule="bookingData.schedule"
            />
          </v-col>
        </v-row>

        <v-row>
          <v-col
            cols="12"
            xl="6"
          >
            <v-textarea
              v-model="bookingData.additionalNotes"
              label="Notizen"
              hide-details
              variant="outlined"
            />
          </v-col>
          <v-col
            v-if="isPrivileged"
            cols="12"
            xl="6"
          >
            <v-textarea
              v-model="bookingData.internalNotes"
              label="Interne Notizen"
              hide-details
              variant="outlined"
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
  FindById200Response,
  RoomRequestDTO,
} from "@/api/raumreservierung-backend";

import {
  mdiAccountSearchOutline,
  mdiCalendarRemoveOutline,
  mdiContentSaveOutline,
  mdiWindowClose,
} from "@mdi/js";
import { computed, onMounted, ref, toRaw } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

import { Levels } from "@/api/error.ts";
import AppointmentCardList from "@/components/booking/AppointmentCardList.vue";
import ExternalPersonSelect from "@/components/booking/ExternalPersonSelect.vue";
import RRuleEditorCard from "@/components/booking/RRuleEditorCard.vue";
import ScheduleTemplateForm from "@/components/booking/ScheduleTemplateForm.vue";
import SeatingTypeSelector from "@/components/booking/SeatingTypeParticipantsSelector.vue";
import BaseView from "@/components/common/BaseView.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CardForm from "@/components/common/CardForm.vue";
import EquipmentSelector from "@/components/rooms/EquipmentSelector.vue";
import RoomSelect from "@/components/rooms/RoomSelect.vue";
import {
  useCreateBooking,
  useGetBooking,
  useUpdateBooking,
} from "@/composables/api/useBookingsApi.ts";
import { useRoomCache } from "@/composables/cache/useRoomCache.ts";
import { useIsPrivileged } from "@/composables/useIsPrivileged.ts";
import { useRules } from "@/composables/useRules.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { ROUTES } from "@/types/Routes.ts";
import {
  EMPTY_BOOKING_REQUEST_DATA,
  mapBookingResponseToRequest,
} from "@/util/bookingTypeUtil.ts";
import { mapResponseToRequest } from "@/util/roomTypeUtil.ts";

const { t } = useI18n();

const rules = useRules();
const route = useRoute();
const router = useRouter();

const isValid = ref<boolean>();
const isSeriesBooking = ref<boolean>(false);

const currentAppointments = ref<AppointmentMinimalResponseDTO[]>([]);
const currentRoom = ref<RoomRequestDTO>();
const bookingData = ref<BookingRequestDTO>(EMPTY_BOOKING_REQUEST_DATA);
const bookedFor = ref<FindById200Response>();

const bookingId = computed(() => (route.params.id as string) || undefined);

const isMyBooking = computed(
  () =>
    route.name === ROUTES.MY_BOOKINGS_EDIT ||
    route.name === ROUTES.MY_BOOKINGS_CREATE
);

const currentRoomSeatingTypeIds = computed(() =>
  currentRoom.value?.roomSeatingCapacities?.map((rSC) => rSC.seatingTypeId)
);

const currentRoomSeatingTypeLimit = computed(
  () =>
    currentRoom.value?.roomSeatingCapacities?.find(
      (rsc) => rsc.seatingTypeId === bookingData.value.seatingTypeId
    )?.capacity ?? 0
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

const {
  call: createBooking,
  loading: createBookingLoading,
  error: createBookingError,
} = useCreateBooking();
useCreateBooking();
const {
  call: updateBooking,
  loading: updateBookingLoading,
  error: updateBookingError,
} = useUpdateBooking();
useUpdateBooking();

const snackbarStore = useSnackbarStore();

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
    bookedFor.value = structuredClone(toRaw(getBookingData.value.bookedFor));
    currentAppointments.value = structuredClone(
      toRaw(getBookingData.value.appointments).slice(0, 7)
    );
    if (getBookingData.value.room?.id) {
      await updateRoom(getBookingData.value.room?.id);
    }
  } else {
    // reset to clear maybe filled out data away
    bookingData.value = EMPTY_BOOKING_REQUEST_DATA;
  }
});

const updateRoom = async (roomId: string | undefined) => {
  if (roomId) {
    currentRoom.value = mapResponseToRequest(await getRoom(roomId));

    if (bookingData?.value.equipmentIds) {
      const filteredEquipmentIds = bookingData.value.equipmentIds.filter(
        (chosenEq) => currentRoom.value?.equipmentIds?.includes(chosenEq)
      );

      bookingData.value = {
        ...bookingData.value,
        equipmentIds: filteredEquipmentIds,
        seatingTypeId:
          bookingData?.value?.seatingTypeId &&
          currentRoomSeatingTypeIds.value?.includes(
            bookingData.value.seatingTypeId
          )
            ? bookingData.value.seatingTypeId
            : undefined,
      };
    }
  }
};

const saveBooking = async () => {
  if (bookingId.value) {
    await updateBooking({
      bookingId: bookingId.value,
      bookingRequestDTO: {
        ...bookingData.value,
        bookedForId: bookedFor.value?.id,
        seatingTypeId: currentRoom.value
          ? bookingData.value.seatingTypeId
          : undefined,
      },
    });

    if (!updateBookingError.value) {
      onSuccess(t("generics.updated", { domain: t("domain.booking.header") }));
    }
  } else {
    await createBooking({
      bookingRequestDTO: {
        ...bookingData.value,
        bookedForId: bookedFor.value?.id,
        seatingTypeId: currentRoom.value
          ? bookingData.value.seatingTypeId
          : undefined,
      },
    });

    if (!createBookingError.value) {
      onSuccess(t("generics.created", { domain: t("domain.booking.header") }));
    }
  }
};

const onSuccess = (msg: string) => {
  snackbarStore.add({ message: msg, level: Levels.SUCCESS });

  router.back();
};

const updateRRule = (value: boolean | null) => {
  if (!value) {
    bookingData.value = {
      ...bookingData.value,
      recurringRule: "",
    };
  }
};
</script>

<style scoped></style>
