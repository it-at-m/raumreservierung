<template>
  <v-select
    v-model="model"
    :items="statusOptions"
    :label="label"
    :disabled="disabled || loading"
    :loading="loading"
    hide-selected
    item-value="value"
    item-title="title"
    variant="outlined"
    hide-details
  >
    <template #selection="{ item }">
      <v-chip
        v-if="item?.value"
        :color="item.color"
        variant="outlined"
        class="font-weight-bold justify-space-evenly"
        style="width: 145px"
        :prepend-icon="item.icon"
        :text="item.title"
      />
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
            variant="outlined"
            class="font-weight-bold justify-space-evenly"
            style="width: 135px"
            :prepend-icon="item.icon"
            :text="item.title"
          />
        </template>
      </v-list-item>
    </template>
    <template #no-data>
      <v-list-item>
        <v-list-item-title class="text-grey-darken-1 text-center">
          Kein weiterer Status verfügbar
        </v-list-item-title>
      </v-list-item>
    </template>
  </v-select>
</template>

<script setup lang="ts">
import { computed } from "vue";

import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend/models/BookingStatusDTO";
import { useBookingStatusConfig } from "@/composables/useBookingStatus.ts";

const model = defineModel<BookingStatusDTOCurrentStatusEnum>({
  required: true,
});

const {
  label = "",
  disabled = false,
  loading = false,
  possibleStatus = [],
} = defineProps<{
  label?: string;
  disabled?: boolean;
  loading?: boolean;
  possibleStatus?: BookingStatusDTOCurrentStatusEnum[];
}>();

const { statusConfig } = useBookingStatusConfig();

const statusOptions = computed(() => {
  const baseList =
    possibleStatus && possibleStatus.length > 0 ? possibleStatus : [];
  // Exclude CANCELED unless currently selected (uses dedicated cancel button).
  const filteredList = baseList.filter(
    (status: string) =>
      status !== BookingStatusDTOCurrentStatusEnum.CANCELED ||
      status === model.value
  );
  return filteredList.map((status: string | undefined) => {
    const style = statusConfig.value.get(status);
    return {
      value: status,
      title: style.text,
      color: style.color,
      icon: style.icon,
    };
  });
});
</script>

<style scoped></style>
