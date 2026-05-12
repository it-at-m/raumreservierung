<template>
  <card-form
    :subtitle="t('domain.seatingType.header')"
    :loading="getAllSeatingTypesLoading"
  >
    <template #text>
      <v-list density="compact">
        <v-list-item
          class="mb-2"
          rounded
          v-for="seatCap in combinedSeatingTypesCapacities"
          :key="seatCap.id"
        >
          <template #default>
            <seating-capacity-editor
              :model-value="seatCap"
              :max-room-capacity="maxRoomCapacity"
              :selectable-seating-types="filteredSeatingTypes"
              @update:model-value="(el) => handleUpdate(el, seatCap.id)"
              @delete="handleRemove"
            />
          </template>
        </v-list-item>
        <v-list-item
          v-if="filteredSeatingTypes && filteredSeatingTypes.length > 0"
        >
          <template #default>
            <seating-capacity-editor
              :model-value="newSeatCap"
              :max-room-capacity="maxRoomCapacity"
              :selectable-seating-types="filteredSeatingTypes"
              @update:model-value="(el) => handleUpdate(el, undefined)"
            />
          </template>
        </v-list-item>
      </v-list>
    </template>
    <template #actions>
      <div class="w-100 d-flex justify-end">
        <v-dialog
          max-width="900"
          persistent
        >
          <template #activator="{ props: activatorProps }">
            <base-button
              v-bind="activatorProps"
              class="mb-2 mr-2"
              secondary
              :prepend-icon="mdiPlus"
              :text="
                t('generics.add', { domain: t('domain.seatingType.header') })
              "
            />
          </template>
          <template #default="{ isActive }">
            <room-resource-management-card
              :item-list="seatingTypes"
              :loading="getAllSeatingTypesLoading || createSeatingTypeLoading"
              :domain="t('domain.seatingType.header')"
              @update-items="getAllSeatingTypes"
              @close="isActive.value = false"
              @create="handleCreate"
            />
          </template>
        </v-dialog>
      </div>
    </template>
  </card-form>
</template>

<script setup lang="ts">
import type { SeatingCapacityRequestDTO } from "@/api/raumreservierung-backend";
import type { SeatingTypeWithCapacity } from "@/types/SeatingTypeWithCapacity.ts";

import { mdiPlus } from "@mdi/js";
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";

import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CardForm from "@/components/common/CardForm.vue";
import RoomResourceManagementCard from "@/components/rooms/RoomResourceManagementCard.vue";
import SeatingCapacityEditor from "@/components/rooms/SeatingCapacityEditor.vue";
import {
  useCreateSeatingType,
  useGetAllSeatingTypes,
} from "@/composables/api/useSeatingApi.ts";

const modelValue = defineModel<SeatingCapacityRequestDTO[]>({
  required: true,
});

const newSeatCap = ref<SeatingTypeWithCapacity | undefined>();

const { maxRoomCapacity = -1 } = defineProps<{
  maxRoomCapacity?: number;
}>();

const {
  call: getAllSeatingTypes,
  data: seatingTypes,
  loading: getAllSeatingTypesLoading,
} = useGetAllSeatingTypes();

const { call: createSeatingType, loading: createSeatingTypeLoading } =
  useCreateSeatingType();

const { t } = useI18n();

const combinedSeatingTypesCapacities = computed(() => {
  if (seatingTypes.value) {
    return modelValue.value.map((seatCap) => {
      const seatType = seatingTypes.value?.find(
        (el) => el.id === seatCap.seatingTypeId
      );

      return {
        ...seatType,
        capacity: seatCap.capacity,
      } as SeatingTypeWithCapacity;
    });
  }
  return [];
});

const filteredSeatingTypes = computed(() =>
  seatingTypes.value?.filter(
    (seatType) =>
      !modelValue.value.some((seatCap) => seatCap.seatingTypeId === seatType.id)
  )
);

const handleUpdate = (
  value: SeatingTypeWithCapacity | undefined,
  originalId: string | undefined
) => {
  if (!value?.id) {
    return;
  }

  if (!originalId) {
    // create
    modelValue.value = [
      ...modelValue.value,
      {
        seatingTypeId: value.id,
        capacity: value.capacity,
      },
    ];
    newSeatCap.value = undefined;
  } else {
    // update
    const index = modelValue.value.findIndex(
      (el) => el.seatingTypeId === originalId
    );
    const tmpArr = [...modelValue.value];

    tmpArr[index] = {
      seatingTypeId: value.id,
      capacity: value.capacity,
    };
    modelValue.value = tmpArr;
  }
};

const handleRemove = (toRemove: SeatingTypeWithCapacity) => {
  modelValue.value = modelValue.value.filter(
    (sc) => sc.seatingTypeId !== toRemove.id
  );
};

const handleCreate = async (newItemName: string) => {
  await createSeatingType({
    seatingTypeRequestDto: {
      name: newItemName,
      description: "",
      isActive: true,
    },
  });

  await getAllSeatingTypes();
};

onMounted(async () => await getAllSeatingTypes());
</script>

<style scoped></style>
