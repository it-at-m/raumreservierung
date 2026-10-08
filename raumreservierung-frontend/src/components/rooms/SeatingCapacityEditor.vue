<template>
  <v-row>
    <v-col cols="6">
      <v-select
        placeholder="Neue Raumbestuhlung hinzufügen"
        color="accent"
        density="compact"
        :model-value="modelValue?.id"
        :items="availableOptions"
        item-value="id"
        item-title="name"
        variant="outlined"
        hide-details
        @update:model-value="handleUpdateSeatingType"
      />
    </v-col>
    <v-col cols="4">
      <v-number-input
        :min="1"
        :suffix="t('domain.person.header', { count: modelValue?.capacity })"
        :max="maxRoomCapacity"
        :disabled="!modelValue?.id"
        color="accent"
        variant="outlined"
        :model-value="modelValue?.capacity || 1"
        density="compact"
        hide-details
        @update:model-value="handleUpdateCapacity"
      />
    </v-col>
    <v-col
      cols="2"
      align-self="center"
    >
      <base-button
        :disabled="!modelValue?.id"
        :prepend-icon="mdiTrashCanOutline"
        :loading="deleteCheckLoading"
        class="w-100"
        secondary
        text="Entfernen"
        @click="handleDelete"
      />
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import type { SeatingTypeResponseDto } from "@/api/raumreservierung-backend";
import type { SeatingTypeWithCapacity } from "@/types/SeatingTypeWithCapacity.ts";

import { mdiTrashCanOutline } from "@mdi/js";
import { computed } from "vue";
import { useI18n } from "vue-i18n";

import { Levels } from "@/api/error.ts";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import { useCheckSeatingTypeDeletableMutation } from "@/composables/api/useSeatingApi.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const modelValue = defineModel<SeatingTypeWithCapacity>();

const { t } = useI18n();

const { maxRoomCapacity, selectableSeatingTypes = [] } = defineProps<{
  maxRoomCapacity: number;
  selectableSeatingTypes?: SeatingTypeResponseDto[];
}>();

const emit = defineEmits<{
  delete: [value: SeatingTypeWithCapacity];
}>();

const { mutateAsync: checkDeletable, isPending: deleteCheckLoading } =
  useCheckSeatingTypeDeletableMutation();

// Attaches current element if exist and not yet present inside the list to selectableSeatingTypes
const availableOptions = computed(() =>
  modelValue.value?.id &&
  !selectableSeatingTypes.some((o) => o.id === modelValue.value?.id)
    ? [...selectableSeatingTypes, modelValue.value]
    : selectableSeatingTypes
);
const snackbarStore = useSnackbarStore();

const handleDelete = async () => {
  const id = modelValue.value?.id;
  if (!id || !modelValue.value) return;

  const deletable = await checkDeletable(id);

  if (deletable) {
    emit("delete", modelValue.value);
  } else {
    snackbarStore.add({
      message: t("components.seatingCapacityEditor.futureUsage"),
      level: Levels.WARNING,
    });
  }
};

// Will only be executed if a seatingType was previously selected
const handleUpdateCapacity = (value: number) => {
  if (modelValue.value) {
    modelValue.value = { ...modelValue.value, capacity: value };
  }
};

const handleUpdateSeatingType = (selectedSeatTypeId: string) => {
  const seatType = selectableSeatingTypes.find(
    (seatType) => seatType.id === selectedSeatTypeId
  );

  if (seatType) {
    modelValue.value = {
      ...seatType,
      capacity: modelValue.value?.capacity || 1,
    };
  }
};
</script>

<style scoped></style>
