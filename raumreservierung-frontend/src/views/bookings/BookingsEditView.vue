<template>
  <base-view :header-text="t('views.bookingEditView.header')">
    <template #headerActions>
      <base-button
        class="ml-4"
        :text="t('common.cancel')"
        :prepend-icon="mdiWindowClose"
        secondary
        @click="router.back()"
      />
      <base-button
        v-if="canCancel && !isCanceledOrUnfeasible"
        class="ml-4"
        :text="t('common.rescind')"
        secondary
        @click="handleStatusChange(BookingRequestDTOStatusEnum.CANCELED)"
      >
        <template #prepend>
          <v-icon
            :icon="mdiCalendarRemoveOutline"
            color="statusCanceled"
          />
        </template>
      </base-button>
      <v-dialog
        v-model="isUnfeasibleDialogOpen"
        max-width="800px"
        width="90%"
      >
        <template #activator>
          <base-button
            :disabled="!isValid"
            class="ml-4"
            :text="t('common.save')"
            :append-icon="mdiContentSaveOutline"
            @click="handleSaveClick"
          />
        </template>
        <template #default>
          <confirm-card
            :title="t('domain.booking.statusChange.cardTitle')"
            :subtitle="t('domain.booking.statusChange.cardSubtitle')"
            @cancel="isUnfeasibleDialogOpen = false"
          >
            <template #text="{ disabled }">
              <v-text-field
                v-model="bookingData.reasonForStatusChange"
                :disabled="disabled"
                :label="t('domain.booking.statusChange.enterReason')"
                variant="outlined"
                hide-details
              />
            </template>
            <template #confirm="{ disabled }">
              <base-button
                :disabled="disabled"
                :text="t('common.save')"
                :append-icon="mdiContentSaveOutline"
                @click="handleUnfeasibleConfirm"
              />
            </template>
          </confirm-card>
        </template>
      </v-dialog>
    </template>
    <template #default>
      <v-form
        v-model="isValid"
        :disabled="createBookingLoading || updateBookingLoading"
        :readonly="isCanceledOrUnfeasible"
      >
        <v-row>
          <v-col
            cols="12"
            :md="isPrivileged ? 6 : 12"
          >
            <v-text-field
              v-model="bookingData.title"
              :rules="[
                rules.required(
                  t('common.rules.notEmpty', {
                    field: t('views.bookingEditView.eventTitle'),
                  })
                ),
              ]"
              :label="t('views.bookingEditView.eventTitle')"
              hide-details="auto"
            />
          </v-col>

          <v-col
            v-if="isPrivileged"
            :cols="showStatus ? 6 : 12"
            :md="showStatus ? 3 : 6"
          >
            <v-select
              v-model="bookingData.bookingType"
              :items="bookingTypeOptions"
              :label="t('domain.booking.typeLong')"
              variant="outlined"
              hide-selected
              hide-details
            >
            </v-select>
          </v-col>
          <v-col
            v-if="showStatus"
            cols="6"
            md="3"
          >
            <general-status-select
              v-model="bookingData.status"
              :label="t('domain.booking.statusTitle')"
              :loading="
                getBookingLoading ||
                createBookingLoading ||
                updateBookingLoading
              "
              :possible-status="statusFull?.nextPossibleStatus"
              :excluded-status="BookingStatusDTOCurrentStatusEnum.CANCELED"
              hide-details
              :readonly="isCanceledOrUnfeasible && !isPrivileged"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <room-select
              v-model="bookingData.roomId"
              :label="t('views.bookingEditView.roomSelect')"
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
                hide-details
                :min="1"
                :max="currentRoomSeatingTypeLimit"
                :suffix="
                  t('domain.booking.participants', {
                    count: 1,
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
                  :label="t('views.bookingEditView.cateringPlanned')"
                />
              </template>
            </card-form>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <card-form
              :subtitle="t('views.bookingEditView.bookedForOthersCard')"
            >
              <template #text>
                <v-row>
                  <v-col
                    cols="12"
                    md="6"
                  >
                    <person-select
                      v-model="bookedFor"
                      :type="InternalPersonRequestDtoTypeEnum.INTERNAL"
                    />
                  </v-col>
                  <v-col
                    cols="12"
                    md="6"
                  >
                    <person-select
                      v-model="bookedFor"
                      :type="InternalPersonRequestDtoTypeEnum.EXTERNAL"
                    />
                  </v-col>
                </v-row>
              </template>
            </card-form>
          </v-col>
        </v-row>
        <v-row>
          <v-col
            cols="12"
            :xl="bookingId && bookingData.recurringRule ? 7 : 12"
          >
            <card-form
              :subtitle="t('views.bookingEditView.dateTimeCard')"
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
                      :label="t('domain.booking.recurrenceRule')"
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
            v-if="bookingId && bookingData.recurringRule"
            cols="12"
            xl="5"
          >
            <appointment-card-list
              :schedule="bookingData.schedule"
              :booking-id="bookingId"
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
              :label="t('domain.booking.notes')"
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
              :label="t('domain.booking.internalNotes')"
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
  BookingRequestDTO,
  FindById200Response,
  RoomRequestDTO,
} from "@/api/raumreservierung-backend";

import {
  mdiCalendarRemoveOutline,
  mdiContentSaveOutline,
  mdiWindowClose,
} from "@mdi/js";
import { computed, onMounted, ref, toRaw, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

import { Levels } from "@/api/error.ts";
import {
  BookingRequestDTOBookingTypeEnum,
  BookingRequestDTOStatusEnum,
  BookingStatusDTOCurrentStatusEnum,
  InternalPersonRequestDtoTypeEnum,
} from "@/api/raumreservierung-backend";
import { type BookingStatusDTO as BookingStatusFull } from "@/api/raumreservierung-backend/models/BookingStatusDTO";
import AppointmentCardList from "@/components/booking/AppointmentCardList.vue";
import GeneralStatusSelect from "@/components/booking/GeneralStatusSelect.vue";
import PersonSelect from "@/components/booking/PersonSelect.vue";
import RRuleEditorCard from "@/components/booking/RRuleEditorCard.vue";
import ScheduleTemplateForm from "@/components/booking/ScheduleTemplateForm.vue";
import SeatingTypeSelector from "@/components/booking/SeatingTypeParticipantsSelector.vue";
import BaseView from "@/components/common/BaseView.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CardForm from "@/components/common/CardForm.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";
import EquipmentSelector from "@/components/rooms/EquipmentSelector.vue";
import RoomSelect from "@/components/rooms/RoomSelect.vue";
import {
  useCreateBooking,
  useGetBooking,
  useUpdateBooking,
} from "@/composables/api/useBookingsApi.ts";
import { useGetRoom } from "@/composables/api/useRoomsApi.ts";
import { useBookingType } from "@/composables/useBookingType.ts";
import { useIsPrivileged } from "@/composables/useIsPrivileged.ts";
import { useRules } from "@/composables/useRules.ts";
import { EMPTY_BOOKING_STATUS_DATA } from "@/constants/BookingStatus";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { useUserStore } from "@/stores/user.ts";
import { ROUTES } from "@/types/Routes.ts";
import {
  EMPTY_BOOKING_REQUEST_DATA,
  mapBookingResponseToRequest,
} from "@/util/bookingTypeUtil.ts";
import { mapResponseToRequest } from "@/util/roomTypeUtil.ts";

const DEFAULT_RRULE = "FREQ=WEEKLY;INTERVAL=1;BYDAY=MO;COUNT=10";

const { t } = useI18n();

const rules = useRules();
const route = useRoute();
const router = useRouter();

const { bookingTypeOptions } = useBookingType();

const isValid = ref<boolean>();
const isUnfeasibleDialogOpen = ref(false);

const currentRoom = ref<RoomRequestDTO>();
const bookingData = ref<BookingRequestDTO>(EMPTY_BOOKING_REQUEST_DATA);
const bookedFor = ref<FindById200Response>();
const statusFull = ref<BookingStatusFull>(EMPTY_BOOKING_STATUS_DATA);

const handleUnfeasibleConfirm = async () => {
  await saveBooking();
  isUnfeasibleDialogOpen.value = false;
};

const handleStatusChange = async (nextStatus: BookingRequestDTOStatusEnum) => {
  bookingData.value.status = nextStatus;
  await saveBooking();
};

const isSeriesBooking = computed({
  get: () => {
    return !!bookingData.value.recurringRule;
  },
  set: (isChecked: boolean) => {
    bookingData.value.recurringRule = isChecked ? DEFAULT_RRULE : undefined;
  },
});

const showStatus = computed(
  () =>
    isPrivileged &&
    bookingData.value.bookingType == BookingRequestDTOBookingTypeEnum.DEFAULT
);

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
    )?.capacity ??
    currentRoom.value?.capacity ??
    0
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

const roomIdToFetch = computed(() => getBookingData.value?.room?.id);

const { isLoading: getRoomLoading, data: roomReqData } =
  useGetRoom(roomIdToFetch);

watch(
  () => roomReqData.value?.id,
  () => {
    if (roomReqData.value) {
      currentRoom.value = mapResponseToRequest(roomReqData.value);
    }
  }
);

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
    statusFull.value = getBookingData.value.status;

    if (getBookingData.value.room?.id) {
      await updateRoom(getBookingData.value.room?.id);
    }
  } else {
    // reset to clear maybe filled out data away
    bookingData.value = { ...EMPTY_BOOKING_REQUEST_DATA };

    const queryRoomId = route.query.roomId as string | undefined;
    if (queryRoomId) {
      bookingData.value.roomId = queryRoomId;
      await updateRoom(queryRoomId);
    }
  }
});

const updateRoom = async (roomId: string | undefined) => {
  if (roomId) {
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
        status: bookingData.value.status,
        reasonForStatusChange: bookingData.value.reasonForStatusChange,
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
        status: bookingData.value.status,
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

  router.replace({
    name: isMyBooking.value ? ROUTES.MY_BOOKINGS_LIST : ROUTES.BOOKINGS_LIST,
  });
};

const updateRRule = (value: boolean | null) => {
  if (!value) {
    bookingData.value = {
      ...bookingData.value,
      recurringRule: "",
    };
  }
};
const isCanceledOrUnfeasible = computed(
  () =>
    bookingData.value.status === BookingStatusDTOCurrentStatusEnum.CANCELED ||
    bookingData.value.status === BookingStatusDTOCurrentStatusEnum.UNFEASIBLE
);

const canCancel = computed(() => {
  const booking = getBookingData.value;
  if (!booking) {
    return false;
  }

  const userOrgId = useUserStore().user?.lhmObjectID;
  if (!userOrgId) {
    return false;
  }

  const isInternalMatch = (person: FindById200Response) =>
    person?.type === "INTERNAL" && person.organisationId === userOrgId;

  return (
    isInternalMatch(booking.bookedBy) || isInternalMatch(booking.bookedFor)
  );
});

const handleSaveClick = () =>
  bookingData.value.status === BookingRequestDTOStatusEnum.UNFEASIBLE
    ? (isUnfeasibleDialogOpen.value = true)
    : saveBooking();
</script>

<style scoped></style>
