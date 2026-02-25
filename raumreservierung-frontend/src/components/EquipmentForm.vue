<template>
  <v-form
    v-model="isValid"
    :disabled="disabled"
  >
    <v-text-field
      variant="outlined"
      :label="t('domain.equipment.name')"
      :rules="[minTwoChars]"
      class="mb-2"
      v-model="modelValue.name"
    />
    <v-text-field
      variant="outlined"
      :label="t('domain.equipment.description')"
      counter="255"
      :rules="[maxCharLength]"
      v-model="modelValue.description"
    />
  </v-form>
</template>

<script setup lang="ts">
import type { EquipmentResponseDto } from "@/api/raumreservierung-backend";

import { ref } from "vue";
import { useI18n } from "vue-i18n";

const MIN_NAME_LENGTH = 2;
const MAX_DESCRIPTION_LENGTH = 255;

const isValid = ref(false);

const modelValue = defineModel<EquipmentResponseDto>({ required: true });

const { t } = useI18n();

defineProps<{
  disabled?: boolean;
}>();

const minTwoChars = (value: string) =>
  (!!value && value.length >= MIN_NAME_LENGTH) ||
  t("components.equipmentForm.rules.minCharsError", { num: MIN_NAME_LENGTH });

const maxCharLength = (value: string) =>
  value.length <= MAX_DESCRIPTION_LENGTH ||
  t("components.equipmentForm.rules.maxDescriptionError", {
    num: MAX_DESCRIPTION_LENGTH,
  });
</script>

<style scoped></style>
