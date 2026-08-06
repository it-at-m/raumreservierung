<template>
  <v-card flat>
    <template #title>
      {{ title }}
    </template>

    <template #subtitle>
      <v-card-subtitle v-if="id && showId">
        {{ t("common.format.idFormat", { id }) }}
      </v-card-subtitle>
    </template>

    <template #text>
      <v-list
        density="compact"
        bg-color="transparent"
        class="pa-0 mt-2"
      >
        <v-list-item class="px-0">
          <v-list-item-subtitle>
            {{ t("domain.booking.date") }}
          </v-list-item-subtitle>
          <v-list-item-title class="font-weight-medium mt-1">
            <template v-if="isMultiDay">
              {{
                t("common.format.dateRange", {
                  start: useDateFormat(
                    schedule?.occupancyStart,
                    DATE_FORMAT_DDMMYY
                  ).value,
                  end: useDateFormat(schedule?.occupancyEnd, DATE_FORMAT_DDMMYY)
                    .value,
                })
              }}
            </template>
            <template v-else>
              {{
                useDateFormat(schedule?.occupancyStart, DATE_FORMAT_DDMMYY)
                  .value
              }}
            </template>
          </v-list-item-title>
        </v-list-item>

        <v-list-item class="px-0 mt-2">
          <v-list-item-subtitle class="mb-2">
            {{ t("domain.booking.statusTitle") }}
          </v-list-item-subtitle>
          <status-chip
            :status="status?.currentStatus"
            variant="outlined"
          />
        </v-list-item>
        <v-list-item
          v-if="
            status?.currentStatus ===
            BookingStatusDTOCurrentStatusEnum.UNFEASIBLE
          "
          class="px-0 mt-2"
        >
          <v-list-item-subtitle class="mb-2">
            {{ t("domain.booking.statusChange.reason") }}
          </v-list-item-subtitle>
          {{
            reasonForStatusChange ||
            t("domain.booking.statusChange.noReasonForStatusChange")
          }}
        </v-list-item>
        <v-list-item
          v-if="isPrivileged"
          class="px-0 mt-2"
        >
          <v-list-item-subtitle class="mb-2">
            {{ t("domain.booking.typeShort") }}
          </v-list-item-subtitle>
          {{ bookingType }}
        </v-list-item>
      </v-list>
    </template>
  </v-card>
</template>

<script setup lang="ts">
import type {
  BookingStatusDTO,
  ScheduleTemplate,
} from "@/api/raumreservierung-backend";

import { useDateFormat } from "@vueuse/core";
import { computed } from "vue";
import { useI18n } from "vue-i18n";

import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend";
import StatusChip from "@/components/booking/StatusChip.vue";
import { useIsPrivileged } from "@/composables/useIsPrivileged.ts";
import { DATE_FORMAT_DDMMYY } from "@/constants.ts";

const { t } = useI18n();

const showId = useIsPrivileged("bookings:manage");

const { schedule, bookingType } = defineProps<{
  id?: string;
  title?: string;
  schedule?: ScheduleTemplate;
  status?: BookingStatusDTO;
  reasonForStatusChange?: string;
  bookingType?: string;
}>();

const isMultiDay = computed(() => {
  if (!schedule?.occupancyStart || !schedule?.occupancyEnd) return false;
  return (
    schedule.occupancyStart.getDate() !== schedule.occupancyEnd.getDate() ||
    schedule.occupancyStart.getMonth() !== schedule.occupancyEnd.getMonth() ||
    schedule.occupancyStart.getFullYear() !==
      schedule.occupancyEnd.getFullYear()
  );
});

const isPrivileged = useIsPrivileged("bookings:write");
</script>
