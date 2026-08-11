<template>
  <v-select
    v-model="modelValue"
    v-bind="$attrs"
    :multiple="multiple"
    :prepend-inner-icon="mdiDoor"
    :loading="getRoomsLoading || loading"
    :items="filteredRooms"
    item-value="id"
    item-title="name"
    variant="outlined"
    :disabled="getRoomsLoading || loading"
    hide-details="auto"
    @update:model-value="emitRoomData"
  />
</template>

<script setup lang="ts" generic="IsMultiple extends boolean = false">
import type { RoomListResponseDTO } from "@/api/raumreservierung-backend";

import { mdiDoor } from "@mdi/js";
import { computed } from "vue";

import { useGetAllRooms } from "@/composables/api/useRoomsApi.ts";

type RoomDataPayload = IsMultiple extends true
  ? RoomListResponseDTO[]
  : RoomListResponseDTO | undefined;

const modelValue = defineModel<IsMultiple extends true ? string[] : string>();

const emit = defineEmits<{
  "update:roomData": [
    data: IsMultiple extends true
      ? RoomListResponseDTO[]
      : RoomListResponseDTO | undefined,
  ];
}>();

const {
  loading = false,
  showInactive = false,
  multiple = false as IsMultiple,
} = defineProps<{
  loading?: boolean;
  showInactive?: boolean;
  multiple?: IsMultiple;
}>();

const { data: allRooms, isPending: getRoomsLoading } = useGetAllRooms({
  onlyActive: false,
});

// Seems unnecessary but is not. multiple is not a simple type anymore, therefore the vue compiler cannot map `multiple` in the template to `multiple="true"` and maps it as a string.
const isMultipleProp = computed(
  () => multiple === true || (multiple as unknown) === ""
);

const filteredRooms = computed(() => {
  if (!allRooms?.value) {
    return [];
  }

  return allRooms.value.filter((room) => {
    if (showInactive || room.isActive) {
      return true;
    }

    if (multiple && Array.isArray(modelValue.value)) {
      return room.id ? modelValue.value.includes(room.id) : false;
    }

    return room.id === modelValue.value;
  });
});

/**
 * Emits the roomData of the selected room / rooms
 */
const emitRoomData = (value: string | string[] | null | undefined) => {
  const selectedRoomData =
    isMultipleProp.value && Array.isArray(value)
      ? (allRooms.value?.filter((room) => {
          return room.id && value.includes(room.id);
        }) ?? [])
      : allRooms.value?.find((room) => {
          return room.id === value;
        });

  emit("update:roomData", selectedRoomData as RoomDataPayload);
};
</script>
