<template>
  <v-form
    v-model="isValid"
    @update:model-value="updatedValidity"
    :disabled="disabled"
  >
    <v-text-field
      v-model="modelValue.name"
      variant="outlined"
      :label="t('domain.person.name')"
    />
    <v-text-field
      v-model="modelValue.company"
      variant="outlined"
      :label="t('domain.externalPerson.company')"
    />
    <v-text-field
      v-model="modelValue.email"
      variant="outlined"
      :label="t('domain.person.email')"
      :rules="[emailValidator, emailNotEmpty]"
    />
    <v-text-field
      v-model="modelValue.telefonNumber"
      variant="outlined"
      :rules="[telefonNumberValidator]"
      :label="t('domain.person.telefonNumber')"
    />
    <v-row>
      <v-col
        cols="12"
        md="8"
      >
        <v-text-field
          v-model="modelValue.streetAddress"
          variant="outlined"
          :label="t('domain.externalPerson.streetAddress')"
        />
      </v-col>
      <v-col
        cols="12"
        md="4"
      >
        <v-text-field
          v-model="modelValue.postalCodeCity"
          variant="outlined"
          :label="t('domain.externalPerson.postalCodeCity')"
        />
      </v-col>
    </v-row>
  </v-form>
</template>

<script setup lang="ts">
import type { ExternalPersonResponseDto } from "@/api/raumreservierung-backend";

import { ref } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const modelValue = defineModel<ExternalPersonResponseDto>({ required: true });

defineProps<{
  disabled?: boolean;
}>();

const emit = defineEmits<{
  isValid: [value: boolean | null];
}>();

const isValid = ref<boolean | null>(false);

const emailNotEmpty = (value: string) =>
  !!value || t("common.rules.notEmpty", { field: t("domain.person.email") });

const emailValidator = (value: string) =>
  !value ||
  /^(([^<>()[\].,;:\s@"]+(\.[^<>()[\].,;:\s@"]+)*)|(".+"))@(([^<>()[\].,;:\s@"]+\.)+[^<>()[\].,;:\s@"]{2,})$/.test(
    value
  ) ||
  t("common.rules.invalidEmail");

const telefonNumberValidator = (value: string) =>
  !value ||
  /^\+?[0-9\s/()-]{7,20}$/.test(value) ||
  t("common.rules.invalidTelefonNumber");

const updatedValidity = (newIsValid: boolean | null) => {
  isValid.value = newIsValid;
  emit("isValid", newIsValid);
};
</script>

<style scoped></style>
