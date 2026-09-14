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
        +{{ (model?.length ?? 0) - 3 }}
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
  const selectedArr = ([] as AllowedStatus[]).concat(model.value ?? []);
  let baseList = [...possibleStatus];
  // Exclude CANCELED unless currently selected (uses dedicated cancel button).
  if (excludedStatus) {
    baseList = baseList.filter(
      (status: string) => status !== excludedStatus || status === model.value
    );
  }
  const representatives = new Map<string, AllowedStatus>();
  for (const status of baseList) {
    const key = groupBy(status);
    const isSelected = selectedArr.includes(status);
    const currentRepresentative = representatives.get(key);

    if (
      !currentRepresentative ||
      (isSelected && !selectedArr.includes(currentRepresentative))
    ) {
      representatives.set(key, status);
    }
  }

  return baseList.filter(
    (status) => representatives.get(groupBy(status)) === status
  );
});
</script>

<style scoped></style>
