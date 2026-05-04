<template>
  <v-form
    v-model="isValid"
    :disabled="disabled"
    :readonly="readOnly"
    @update:model-value="updatedValidity"
  >
    <v-row>
      <v-col
        cols="12"
        md="3"
        class="pb-0 pb-md-3"
      >
        <v-select
          v-model="modelValue.title"
          :items="titleOptions"
          item-title="label"
          item-value="value"
          variant="outlined"
          :label="t('domain.person.title')"
        />
      </v-col>
      <v-col
        cols="12"
        md="4"
        class="py-0 py-md-3"
      >
        <v-text-field
          v-model="modelValue.firstName"
          variant="outlined"
          :label="t('domain.person.firstName')"
        />
      </v-col>
      <v-col
        cols="12"
        md="5"
        class="pt-0 pt-md-3"
      >
        <v-text-field
          v-model="modelValue.lastName"
          variant="outlined"
          :label="t('domain.person.lastName')"
        />
      </v-col>
    </v-row>
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
        class="pb-0 pb-md-3"
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
        class="pt-0 pt-md-3"
      >
        <v-text-field
          v-model="modelValue.postalCodeCity"
          variant="outlined"
          :label="t('domain.externalPerson.postalCodeCity')"
        />
      </v-col>
    </v-row>
    <v-textarea
      v-model="modelValue.note"
      variant="outlined"
      rows="3"
      class="mb-n4"
      :counter="MAX_NOTE_LENGTH"
      :rules="[maxNoteLength]"
      :label="t('domain.externalPerson.note')"
    />
  </v-form>
</template>

<script setup lang="ts">
import type { ExternalPersonResponseDto } from "@/api/raumreservierung-backend";

import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const modelValue = defineModel<ExternalPersonResponseDto>({ required: true });

defineProps<{
  disabled?: boolean;
  readOnly?: boolean;
}>();

const emit = defineEmits<{
  isValid: [value: boolean | null];
}>();

const MAX_NOTE_LENGTH = 500;

const isValid = ref<boolean | null>(false);

const titleOptions = computed(() => [
  { value: "NONE", label: t("domain.person.titles.none") },
  { value: "MS", label: t("domain.person.titles.ms") },
  { value: "MR", label: t("domain.person.titles.mr") },
  { value: "DIVERSE", label: t("domain.person.titles.diverse") },
]);

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

const maxNoteLength = (value: string) =>
  (value?.length || 0) <= MAX_NOTE_LENGTH ||
  t("common.rules.maxLengthError", {
    field: t("domain.externalPerson.note"),
    num: MAX_NOTE_LENGTH,
  });

const updatedValidity = (newIsValid: boolean | null) => {
  isValid.value = newIsValid;
  emit("isValid", newIsValid);
};
</script>

<style scoped></style>
