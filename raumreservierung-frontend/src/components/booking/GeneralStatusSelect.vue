<template>
  <v-select
    v-model="model"
    :items="statusOptions"
    :label="label"
    :disabled="disabled || loading"
    :loading="loading"
    :hide-selected="!multiple"
    :multiple="multiple"
    item-value="value"
    item-title="title"
    variant="outlined"
    hide-details
    :prepend-inner-icon="multiple ? mdiLabelMultipleOutline : undefined"
  >
    <template #selection="{ item }">
      <status-chip :status="item" />
    </template>

    <template #item="{ item, props }">
      <v-list-item
        v-bind="props"
        density="compact"
      >
        <template #title>
          <status-chip
            :status="item"
            :density="'compact'"
          />
        </template>
      </v-list-item>
    </template>
    <template #no-data>
      <v-list-item>
        <v-list-item-title>
          {{ t("domain.booking.status.notAvailable") }}
        </v-list-item-title>
      </v-list-item>
    </template>
  </v-select>
</template>

<script setup lang="ts">
import { mdiLabelMultipleOutline } from "@mdi/js";
import { computed } from "vue";
import { useI18n } from "vue-i18n";

import {
  BookingStatusDTOCurrentStatusEnum,
  GetBookingsByPageableAndFilterStatusEnum,
} from "@/api/raumreservierung-backend";
import StatusChip from "@/components/booking/StatusChip.vue";

const { t } = useI18n();

type AllowedStatus =
  | GetBookingsByPageableAndFilterStatusEnum
  | BookingStatusDTOCurrentStatusEnum;

const model = defineModel<AllowedStatus | AllowedStatus[] | undefined>();

const {
  label = "",
  disabled = false,
  loading = false,
  possibleStatus = [],
  multiple = false,
  excludeStatus = false,
  excludedStatus = undefined,
} = defineProps<{
  label?: string;
  disabled?: boolean;
  loading?: boolean;
  possibleStatus?: AllowedStatus[];
  multiple?: boolean;
  excludeStatus?: boolean;
  excludedStatus?: AllowedStatus;
}>();

const statusOptions = computed(() => {
  let baseList = [...possibleStatus];
  // Exclude CANCELED unless currently selected (uses dedicated cancel button).
  if (excludeStatus) {
    baseList = baseList.filter(
      (status: string) => status !== excludedStatus || status === model.value
    );
  }
  return baseList;
});
</script>

<style scoped></style>
