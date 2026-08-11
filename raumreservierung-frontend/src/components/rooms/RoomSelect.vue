<template>
  <v-select
    v-model="modelValue"
    v-bind="$attrs"
    :prepend-inner-icon="mdiDoor"
    :loading="getRoomsLoading || loading"
    :items="filteredRooms ?? []"
    item-value="id"
    item-title="name"
    variant="outlined"
    :disabled="getRoomsLoading || loading"
    hide-details="auto"
    :multiple="multiple"
  />
</template>

<script setup lang="ts" generic="IsMultiple extends boolean = false">
import { mdiDoor } from "@mdi/js";
import { computed } from "vue";

import { useGetAllRooms } from "@/composables/api/useRoomsApi.ts";

const modelValue = defineModel<IsMultiple extends true ? string[] : string>();

const {
  loading = false,
  showInactive = false,
  multiple = false as IsMultiple,
} = defineProps<{
  loading?: boolean;
  showInactive?: boolean;
  multiple?: IsMultiple;
}>();

const filteredRooms = computed(
  () =>
    allRooms?.value?.filter(
      (room) => showInactive || room.isActive || room.id === modelValue.value
    ) || []
);

const { data: allRooms, isPending: getRoomsLoading } = useGetAllRooms({
  onlyActive: false,
});
</script>

<style scoped></style>
