<template>
  <v-select
    v-model="model"
    :items="statusOptions"
    :label="label"
    :disabled="disabled || loading"
    :loading="loading"
    item-value="value"
    item-title="title"
    variant="outlined"
    hide-details
  >
    <template #selection="{ item }">
      <v-chip
        v-if="item?.value"
        :color="item.color"
        variant="tonal"
        size="small"
        class="font-weight-bold"
      >
        {{ item.title }}
      </v-chip>
    </template>

    <template #item="{ item, props }">
      <v-list-item
        v-bind="props"
        title=""
      >
        <template #default>
          <v-chip
            v-if="item?.value"
            :color="item.color"
            variant="tonal"
            size="small"
            class="font-weight-bold"
          >
            {{ item.title }}
          </v-chip>
        </template>
      </v-list-item>
    </template>
  </v-select>
</template>

<script setup lang="ts">
import { computed } from "vue";

import { BookingStatusDTOCurrentStatusEnum as BookingStatus } from "@/api/raumreservierung-backend/models/BookingStatusDTO";
import { useBookingStatusStyles } from "@/composables/useBookingStatus.ts";

const model = defineModel<BookingStatus | null>({ required: true });

const { possibleStatus } = defineProps<{
  label?: string;
  disabled?: boolean;
  loading?: boolean;
  possibleStatus?: BookingStatus[];
}>();

const { getStatusStyle } = useBookingStatusStyles();

const ALL_STATUS_VALUES: BookingStatus[] = Object.values(BookingStatus);

const statusOptions = computed(() => {
  const filteredList =
    possibleStatus && possibleStatus.length > 0
      ? possibleStatus
      : ALL_STATUS_VALUES;
  return filteredList.map((status) => {
    const style = getStatusStyle(status);
    return {
      value: status,
      title: style.text,
      color: style.color,
    };
  });
});
</script>

<style scoped></style>
