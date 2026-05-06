<template>
  <v-form
    v-model="isValid"
    :disabled="disabled"
    @update:model-value="updatedValidity"
  >
    <v-text-field
      v-model="modelValue.name"
      :label="t('domain.holidays.public.name')"
      :rules="[maxNameLength, minTwoChars]"
      autofocus
      variant="outlined"
    />
    <card-form :subtitle="t('domain.holidays.public.date')">
      <template #text>
        <v-row justify="start">
          <v-col
            class="pr-10 pb-0"
            cols="12"
            sm="6"
          >
            <v-date-input
              ref="startDateInput"
              v-model="modelValue.startDate"
              :allowed-dates="isDateAllowed('startDate')"
              :label="isPublic ? '' : t('domain.holidays.school.startDate')"
              :prepend-icon="mdiCalendar"
              :rules="[datePicked, dateRules('startDate')]"
              variant="underlined"
              @update:model-value="validateDate('endDate')"
            />
          </v-col>
          <v-col
            v-if="!isPublic"
            class="pr-10 pb-0"
            cols="12"
            sm="6"
          >
            <v-date-input
              ref="endDateInput"
              v-model="modelValue.endDate"
              :allowed-dates="isDateAllowed('endDate')"
              :label="t('domain.holidays.school.endDate')"
              :prepend-icon="mdiCalendar"
              :rules="[datePicked, dateRules('endDate')]"
              variant="underlined"
              @update:model-value="validateDate('startDate')"
            />
          </v-col>
        </v-row>
      </template>
    </card-form>
  </v-form>
</template>

<script lang="ts" setup>
import type { HolidayRequestDTO } from "@/api/raumreservierung-backend";
import type { VDateInput } from "vuetify/labs/VDateInput";

import { mdiCalendar } from "@mdi/js";
import { ref, useTemplateRef } from "vue";
import { useI18n } from "vue-i18n";

import CardForm from "@/components/common/CardForm.vue";

const MIN_NAME_LENGTH = 2;
const MAX_NAME_LENGTH = 100;

const modelValue = defineModel<Partial<HolidayRequestDTO>>({ required: true });
const isValid = ref<boolean | null>(false);
const startDateInput =
  useTemplateRef<InstanceType<typeof VDateInput>>("startDateInput");
const endDateInput =
  useTemplateRef<InstanceType<typeof VDateInput>>("endDateInput");

const { t } = useI18n();

const { isPublic, disabled } = defineProps<{
  isPublic: boolean;
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

const maxNameLength = (value: string) =>
  value.length <= MAX_NAME_LENGTH ||
  t("common.rules.maxLengthError", {
    field: t("domain.equipment.name"),
    num: MAX_NAME_LENGTH,
  });

const ensureDate = (value: string | Date | undefined): Date | undefined => {
  return value ? (value instanceof Date ? value : new Date(value)) : undefined;
};

const datePicked = (value: Date) =>
  value != undefined || t("common.rules.noDateSelected");

const updatedValidity = (newIsValid: boolean | null) => {
  isValid.value = newIsValid;
  emit("isValid", newIsValid);
};

const applyDateRule = (date: Date, type: dateType) => {
  const isStartDate = type === "startDate";
  const other = isStartDate ? "endDate" : "startDate";
  const otherDate = ensureDate(modelValue.value[other]);
  const currentDate = ensureDate(date);

  return (
    isPublic ||
    !otherDate ||
    !currentDate ||
    (isStartDate ? currentDate < otherDate : currentDate > otherDate)
  );
};

const dateRules = (type: dateType) => {
  return (date: Date) => {
    return (
      applyDateRule(date, type) ||
      (type === "startDate"
        ? t("common.rules.startAfterEnd")
        : t("common.rules.endBeforeStart"))
    );
  };
};

const isDateAllowed = (type: dateType) => (date: unknown) => {
  return date instanceof Date && applyDateRule(date, type);
};

type dateType = "startDate" | "endDate";

const validateDate = (type: dateType) => {
  const inputRef = type === "startDate" ? startDateInput : endDateInput;
  if (type === "endDate" && isPublic) {
    modelValue.value.endDate = modelValue.value.startDate;
  }
  if (modelValue.value[type]) {
    inputRef.value?.validate();
  }
};
</script>

<style scoped></style>
