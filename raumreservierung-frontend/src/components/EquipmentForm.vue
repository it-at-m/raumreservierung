<template>
  <v-card
    :title="computedTitle"
    :loading="loading"
  >
    <template #text>
      <v-form
        v-model="isValid"
        :disabled="loading"
      >
        <v-text-field
          variant="outlined"
          label="Name"
          :rules="[minTwoChars]"
          class="mb-2"
          v-model="modelValue.name"
        />
        <v-text-field
          variant="outlined"
          label="Beschreibung"
          counter="255"
          :rules="[maxCharLength]"
          v-model="modelValue.description"
        />
      </v-form>
    </template>
    <template #actions>
      <div class="mr-4 mb-4">
        <base-button
          secondary
          class="mr-4"
          @click="emit('cancel')"
          :disabled="loading"
        >
          <template #default> Abbrechen </template>
          <template #prepend>
            <v-icon :icon="mdiClose" />
          </template>
        </base-button>
        <base-button
          :disabled="!isValid || loading"
          @click="emit('save')"
        >
          <template #default> Speichern </template>
          <template #append>
            <v-icon :icon="mdiContentSaveOutline" />
          </template>
        </base-button>
      </div>
    </template>
  </v-card>
</template>

<script setup lang="ts">
import type { EquipmentResponseDto } from "@/api/raumreservierung-backend";

import { mdiClose, mdiContentSaveOutline } from "@mdi/js";
import { computed, ref } from "vue";

import BaseButton from "@/components/common/buttons/BaseButton.vue";

const isValid = ref(false);

const modelValue = defineModel<EquipmentResponseDto>({ required: true });

const computedTitle = computed(() =>
  modelValue.value.id ? "Ausstattung bearbeiten" : "Ausstattung erstellen"
);

const { loading } = defineProps<{
  loading?: boolean;
}>();

const minTwoChars = (value: string) =>
  (!!value && value.length > 1) || "Minimum zwei Zeichen";

const maxCharLength = (value: string) => value.length < 256 || "Text zu lang";

const emit = defineEmits<(e: "cancel" | "save") => void>();
</script>

<style scoped></style>
