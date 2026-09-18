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
    <template #selection="{ item, index }">
      <status-chip
        v-if="index < 3"
        :status="item"
      />
      <v-chip
        v-if="index === 3"
        size="small"
        variant="outlined"
        color="grey"
      >
        {{ (model?.length ?? 0) - 3 }}
      </v-chip>
    </template>

    <template #item="{ item, props }">
      <v-list-item
        v-bind="props"
        density="compact"
      >
        <template #title>
          <status-chip
            variant="text"
            :status="item"
            density="compact"
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
  possibleStatus = Object.values(GetBookingsByPageableAndFilterStatusEnum),
  multiple = false,
  excludedStatus = undefined,
  groupBy = (status: AllowedStatus) => status as string,
} = defineProps<{
  label?: string;
  disabled?: boolean;
  loading?: boolean;
  possibleStatus?: AllowedStatus[];
  multiple?: boolean;
  excludedStatus?: AllowedStatus;
  groupBy?: (status: AllowedStatus) => string;
}>();

const statusOptions = computed(() => {
  const selected = Array.isArray(model.value)
    ? model.value
    : model.value
      ? [model.value]
      : [];

  // Exclude CANCELED unless currently selected (uses dedicated cancel button).
  const valid = possibleStatus.filter(
    (s) => s !== excludedStatus || selected.includes(s)
  );

  return valid.filter((status, _, arr) => {
    const group = arr.filter((x) => groupBy(x) === groupBy(status));
    const representative = group.find((x) => selected.includes(x)) || group[0];

    return status === representative;
  });
});
</script>

<style scoped></style>
