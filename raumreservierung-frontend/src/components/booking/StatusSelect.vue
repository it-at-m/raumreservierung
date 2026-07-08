<template>
  <v-select
    v-model="model"
    :items="statusOptions"
    :label="label"
    item-value="value"
    item-title="title"
    variant="outlined"
    hide-details
    multiple
    :prepend-inner-icon="mdiLabelMultipleOutline"
  >
    <template #selection="{ item }">
      <v-chip
        v-if="item?.value"
        :color="item.color"
        variant="outlined"
        class="font-weight-bold justify-space-evenly"
        style="width: 105px"
        size="x-small"
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
            size="small"
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
import type { GetBookingsByPageableAndFilterStatusEnum } from "@/api/raumreservierung-backend";

import { mdiLabelMultipleOutline } from "@mdi/js";
import { computed } from "vue";

import { useBookingStatusConfig } from "@/composables/useBookingStatus.ts";

const model = defineModel<
  GetBookingsByPageableAndFilterStatusEnum[] | undefined
>({});

const { label = "", status = [] } = defineProps<{
  label?: string;
  status?: GetBookingsByPageableAndFilterStatusEnum[];
}>();

const { statusConfig } = useBookingStatusConfig();

const statusOptions = computed(() => {
  const baseList = status && status.length > 0 ? status : [];
  return baseList.map((status) => {
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
