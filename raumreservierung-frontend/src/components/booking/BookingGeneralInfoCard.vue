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
            {{ t("domain.booking.status") }}
          </v-list-item-subtitle>
          <v-chip
            v-if="status?.currentStatus"
            :color="statusConfig.get(status.currentStatus).color"
            variant="outlined"
            class="font-weight-bold justify-space-evenly"
            :prepend-icon="statusConfig.get(status.currentStatus).icon"
            :text="statusConfig.get(status.currentStatus).text"
            style="width: 135px"
          />
        </v-list-item>
        <v-list-item
          v-if="status?.currentStatus === 'UNFEASIBLE'"
          class="px-0 mt-2"
        >
          <v-list-item-subtitle class="mb-2">
            {{ "Grund" }}
          </v-list-item-subtitle>
          {{ reasonForRejection || "Kein Grund angegeben" }}
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

import { useBookingStatusConfig } from "@/composables/useBookingStatus.ts";
import { useIsPrivileged } from "@/composables/useIsPrivileged.ts";
import { DATE_FORMAT_DDMMYY } from "@/constants.ts";

const { t } = useI18n();

const showId = useIsPrivileged("bookings:manage");

const { statusConfig } = useBookingStatusConfig();

const props = defineProps<{
  id?: string;
  title?: string;
  schedule?: ScheduleTemplate;
  status?: BookingStatusDTO;
  reasonForRejection?: string;
}>();

const isMultiDay = computed(() => {
  if (!props.schedule?.occupancyStart || !props.schedule?.occupancyEnd)
    return false;
  return (
    props.schedule.occupancyStart.getDate() !==
      props.schedule.occupancyEnd.getDate() ||
    props.schedule.occupancyStart.getMonth() !==
      props.schedule.occupancyEnd.getMonth() ||
    props.schedule.occupancyStart.getFullYear() !==
      props.schedule.occupancyEnd.getFullYear()
  );
});
</script>
