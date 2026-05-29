<template>
  <v-select
    v-model="modelValue"
    v-bind="$attrs"
    :prepend-inner-icon="mdiDoor"
    :loading="getRoomsLoading || loading"
    :items="allRooms ?? []"
    item-value="id"
    item-title="name"
    label="Nach Raum filtern"
    variant="outlined"
    :disabled="getRoomsLoading || loading"
    hide-details
  />
</template>

<script setup lang="ts">
import { mdiDoor } from "@mdi/js";
import { onMounted } from "vue";

import { useGetAllRooms } from "@/composables/api/useRoomsApi.ts";

const modelValue = defineModel<string>();

defineProps<{
  loading?: boolean;
}>();

const {
  call: getRooms,
  data: allRooms,
  loading: getRoomsLoading,
} = useGetAllRooms();

onMounted(async () => {
  await getRooms();
});
</script>

<style scoped></style>
