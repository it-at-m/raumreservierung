<template>
  <v-card :loading="loading">
    <v-card-item
      :class="`bg-${config.color}`"
      :title="booking?.title"
      :append-icon="mdiCalendarQuestionOutline"
    />
    <v-card-text>
      <v-row class="mt-2">
        <v-col
          cols="12"
          sm="6"
          md="4"
          lg="3"
        >
          <icon-information
            class="mb-2"
            :icon="mdiAccountOutline"
            :text="
              t('common.format.fullName', {
                firstName: booking?.bookedFor.firstName,
                lastName: booking?.bookedFor.lastName,
              })
            "
          />
          <icon-information
            v-if="booking?.schedule.occupancyStart"
            class="my-2"
            :icon="mdiCalendarStartOutline"
            :text="toDateString(booking?.schedule.occupancyStart)"
          />
          <icon-information
            v-if="
              booking?.schedule.occupancyStart && booking?.schedule.occupancyEnd
            "
            class="my-2"
            :icon="mdiCalendarRangeOutline"
            :text="
              t('common.format.dateRange', {
                start: toTimeString(booking?.schedule.occupancyStart),
                end: toTimeString(booking?.schedule.occupancyEnd),
              })
            "
          />
          <icon-information
            class="my-2"
            :icon="mdiAccountOutline"
            :text="
              t('domain.booking.participantCount', {
                count: booking?.participantCount,
              })
            "
          />
          <icon-information
            class="my-2"
            :icon="
              booking?.recurringRule
                ? mdiCalendarRefreshOutline
                : mdiCalendarBlankOutline
            "
            :text="
              booking?.recurringRule
                ? t('domain.booking.appointmentType.series')
                : t('domain.booking.appointmentType.single')
            "
          />
          <icon-information
            v-if="booking?.seatingType"
            class="my-2"
            :icon="mdiSofaSingleOutline"
            :text="booking?.seatingType?.name"
          />
        </v-col>
        <v-col
          cols="12"
          sm="6"
          md="4"
          lg="3"
        >
          <div class="text-title-small text-medium-emphasis mb-2 ml-n1">
            <icon-information
              :text="t('components.bookingDetailsSummary.annotation')"
              :icon="mdiCommentTextOutline"
            />
          </div>
          <div>
            {{ booking?.additionalNotes }}
          </div>
        </v-col>
        <v-col
          v-if="booking?.equipments?.length"
          cols="12"
          sm="12"
          md="12"
          lg="6"
        >
          <div class="text-title-small text-medium-emphasis mb-2 ml-n1">
            <icon-information
              :text="t('components.bookingDetailsSummary.selectedEquipment')"
              :icon="mdiStoreCheckOutline"
            />
          </div>
          <v-row density="comfortable">
            <v-col
              v-for="item in booking?.equipments"
              :key="item.name"
              cols="6"
              sm="4"
              md="3"
              lg="4"
            >
              <div class="text-body-2 d-flex align-center">
                {{ item.name }}
              </div>
            </v-col>
          </v-row>
        </v-col>
      </v-row>
    </v-card-text>
    <v-divider />
    <v-card-actions>
      <v-row
        justify="space-between"
        class="ma-1"
      >
        <v-col cols="auto">
          <base-button
            v-if="isBookingEditable"
            color="statusCanceled"
            :disabled="loading"
            :prepend-icon="mdiCalendarRemoveOutline"
            secondary
            :text="t('common.refuse')"
            @click="emitNewBookingStatus('UNFEASIBLE')"
          />
        </v-col>
        <v-col
          v-if="!bookingChanged"
          cols="auto"
        >
          <base-button
            secondary
            :text="t('common.edit')"
            :append-icon="mdiPencil"
            :disabled="loading"
            @click="
              router.push({
                name: ROUTES.BOOKINGS_EDIT,
                params: { id: booking?.id },
              })
            "
          />
          <base-button
            v-if="
              isBookingEditable &&
              (isRO || isRA) &&
              equalsAnyBookingStatus(['ROOM_CHANGED', 'NEW'])
            "
            secondary
            :disabled="loading"
            color="statusRequested"
            :append-icon="mdiCalendarLockOutline"
            class="ml-2"
            :text="t('common.reserve')"
            @click="
              emitNewBookingStatus(
                booking?.status.currentStatus === 'NEW'
                  ? 'ROOM_APPROVED'
                  : 'ORGANIZER_CHANGED'
              )
            "
          />
          <base-button
            v-if="
              isBookingEditable &&
              isTO &&
              equalsAnyBookingStatus([
                'COORDINATION_NEEDED',
                'ROOM_APPROVED',
                'ORGANIZER_CHANGED',
              ])
            "
            :disabled="loading"
            secondary
            class="ml-2"
            color="statusApproved"
            :append-icon="mdiCalendarCheckOutline"
            :text="t('common.book')"
            @click="emitNewBookingStatus('ORGANIZER_APPROVED')"
          />
        </v-col>
        <v-col
          v-else
          cols="auto"
        >
          <base-button
            secondary
            :disabled="loading"
            :text="t('common.cancel')"
            @click="emit('cancel')"
          />
          <base-button
            class="ml-2"
            :disabled="loading"
            :text="t('common.save')"
            @click="emit('confirm')"
          />
        </v-col>
      </v-row>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type {
  BookingDetailResponseDTO,
  BookingStatusDTOCurrentStatusEnum,
} from "@/api/raumreservierung-backend";

import {
  mdiAccountOutline,
  mdiCalendarBlankOutline,
  mdiCalendarCheckOutline,
  mdiCalendarLockOutline,
  mdiCalendarQuestionOutline,
  mdiCalendarRangeOutline,
  mdiCalendarRefreshOutline,
  mdiCalendarRemoveOutline,
  mdiCalendarStartOutline,
  mdiCommentTextOutline,
  mdiPencil,
  mdiSofaSingleOutline,
  mdiStoreCheckOutline,
} from "@mdi/js";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

import BaseButton from "@/components/common/buttons/BaseButton.vue";
import IconInformation from "@/components/common/IconInformation.vue";
import {
  useBookingStatusConfig,
  useIsBookingEditable,
} from "@/composables/useBookingStatus.ts";
import { useHasRole } from "@/composables/useIsPrivileged.ts";
import { ROUTES } from "@/types/Routes.ts";
import { toDateString, toTimeString } from "@/util/formatter.ts";

const { t } = useI18n();

const router = useRouter();

const {
  booking,
  loading = false,
  bookingChanged = false,
} = defineProps<{
  booking?: BookingDetailResponseDTO;
  loading?: boolean;
  bookingChanged?: boolean;
}>();

const isTO = useHasRole("terminorganisator");
const isRO = useHasRole("raumbuchung");
const isRA = useHasRole("raumadmin");

const isBookingEditable = useIsBookingEditable(() => booking);

const { config } = useBookingStatusConfig(() => booking?.status.currentStatus);

const emit = defineEmits<{
  statusAction: [newStatus: BookingStatusDTOCurrentStatusEnum];
  confirm: [];
  cancel: [];
}>();

const equalsAnyBookingStatus = (
  status:
    | BookingStatusDTOCurrentStatusEnum
    | BookingStatusDTOCurrentStatusEnum[]
): boolean => {
  if (booking?.status.currentStatus) {
    const statusArr = Array.isArray(status) ? status : [status];
    return statusArr.includes(booking?.status.currentStatus);
  }
  return false;
};

const emitNewBookingStatus = async (
  newStatus: BookingStatusDTOCurrentStatusEnum
) => {
  emit("statusAction", newStatus);
};
</script>

<style scoped></style>
