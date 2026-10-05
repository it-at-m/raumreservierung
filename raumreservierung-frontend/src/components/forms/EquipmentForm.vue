<template>
  <v-form
    v-model="isValid"
    :disabled="disabled"
    @update:model-value="updatedValidity"
  >
    <v-text-field
      v-model="modelValue.name"
      variant="outlined"
      :label="t('domain.equipment.name')"
      :rules="[minTwoChars, maxNameLength]"
      class="mb-2"
      autofocus
    />
    <v-text-field
      v-model="modelValue.description"
      variant="outlined"
      :label="t('domain.equipment.description')"
      counter="255"
      :rules="[maxDescriptionLength]"
    />
  </v-form>
  <card-form :subtitle="t('domain.equipment.isActive')">
    <template #text>
      <v-checkbox
        v-model="modelValue.isActive"
        density="compact"
        hide-details
        :label="
          t('generics.isActivated', {
            domain: t('domain.equipment.header'),
          })
        "
      />
    </template>
  </card-form>
</template>

<script setup lang="ts">
import type { EquipmentResponseDto } from "@/api/raumreservierung-backend";

import { ref } from "vue";
import { useI18n } from "vue-i18n";

import CardForm from "@/components/common/CardForm.vue";

const MIN_NAME_LENGTH = 2;
const MAX_DESCRIPTION_LENGTH = 255;
const MAX_NAME_LENGTH = 100;

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
  t("common.rules.minLengthError", {
    field: t("domain.equipment.name"),
    num: MIN_NAME_LENGTH,
  });

const maxDescriptionLength = (value: string) =>
  value.length <= MAX_DESCRIPTION_LENGTH ||
  t("common.rules.maxLengthError", {
    field: t("domain.equipment.description"),
    num: MAX_DESCRIPTION_LENGTH,
  });

const maxNameLength = (value: string) =>
  value.length <= MAX_NAME_LENGTH ||
  t("common.rules.maxLengthError", {
    field: t("domain.equipment.name"),
    num: MAX_NAME_LENGTH,
  });
const updatedValidity = (newIsValid: boolean | null) => {
  isValid.value = newIsValid;
  emit("isValid", newIsValid);
};
</script>

<style scoped></style>
