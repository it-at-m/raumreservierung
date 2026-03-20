<template>
  <v-form
    :model-value="isValid"
    @update:model-value="updatedValidity"
    :disabled="disabled"
  >
    <v-text-field
      variant="outlined"
      :label="t('domain.seatingType.name')"
      :rules="[minTwoChars, maxNameLength]"
      class="mb-2"
      v-model="modelValue.name"
      autofocus
    />
    <v-text-field
      variant="outlined"
      :label="t('domain.seatingType.description')"
      counter="255"
      :rules="[maxDescriptionLength]"
      v-model="modelValue.description"
    />
    <card-form :subtitle="t('domain.seatingType.isActive')">
      <template #text>
        <v-checkbox
          density="compact"
          v-model="modelValue.isActive"
          hide-details
          :label="
            t('generics.isActivated', {
              domain: t('domain.seatingType.header'),
            })
          "
        />
      </template>
    </card-form>
  </v-form>
</template>

<script setup lang="ts">
import type { SeatingTypeResponseDto } from "@/api/raumreservierung-backend";

import { ref } from "vue";
import { useI18n } from "vue-i18n";

import CardForm from "@/components/common/CardForm.vue";

const MIN_NAME_LENGTH = 2;
const MAX_DESCRIPTION_LENGTH = 255;
const MAX_NAME_LENGTH = 100;

const isValid = ref<boolean | null>(false);

const modelValue = defineModel<SeatingTypeResponseDto>({ required: true });

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
    field: t("domain.seatingType.name"),
    num: MIN_NAME_LENGTH,
  });

const maxDescriptionLength = (value: string) =>
  value.length <= MAX_DESCRIPTION_LENGTH ||
  t("common.rules.maxLengthError", {
    field: t("domain.seatingType.description"),
    num: MAX_DESCRIPTION_LENGTH,
  });

const maxNameLength = (value: string) =>
  value.length <= MAX_NAME_LENGTH ||
  t("common.rules.maxLengthError", {
    field: t("domain.seatingType.name"),
    num: MAX_NAME_LENGTH,
  });

const updatedValidity = (newIsValid: boolean | null) => {
  isValid.value = newIsValid;
  emit("isValid", newIsValid);
};
</script>

<style scoped></style>
