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
    hide-details
  />
</template>

<script setup lang="ts">
import { mdiDoor } from "@mdi/js";
import { computed, onMounted } from "vue";

import { useGetAllRooms } from "@/composables/api/useRoomsApi.ts";

const modelValue = defineModel<string>();

const { loading = false, showInactive = false } = defineProps<{
  loading?: boolean;
  showInactive?: boolean;
}>();

const filteredRooms = computed(
  () =>
    allRooms?.value?.filter(
      (room) => showInactive || room.isActive || room.id === modelValue.value
    ) || []
);

const { data: allRooms, isPending: getRoomsLoading } = useGetAllRooms({ onlyActive:false});
</script>

<style scoped></style>
