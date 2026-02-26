<template>
  <v-form
    v-model="isValid"
    @update:model-value="updatedValidity"
    :disabled="disabled"
  >
    <v-text-field
      variant="outlined"
      :label="t('domain.equipment.name')"
      :rules="[minTwoChars]"
      class="mb-2"
      v-model="modelValue.name"
      autofocus
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

const isValid = ref<boolean | null>(false);

const modelValue = defineModel<EquipmentResponseDto>({ required: true });

const { t } = useI18n();

defineProps<{
  disabled?: boolean;
}>();

const emit = defineEmits<{
  isValid: [value: boolean | null];
}>();

const minTwoChars = (value: string) =>
  (!!value && value.length >= MIN_NAME_LENGTH) ||
  t("components.equipmentForm.rules.minCharsError", { num: MIN_NAME_LENGTH });

const maxCharLength = (value: string) =>
  value.length <= MAX_DESCRIPTION_LENGTH ||
  t("components.equipmentForm.rules.maxDescriptionError", {
    num: MAX_DESCRIPTION_LENGTH,
  });

const updatedValidity = (newIsValid: boolean | null) => {
  isValid.value = newIsValid;
  emit("isValid", newIsValid);
};
</script>

<style scoped></style>
