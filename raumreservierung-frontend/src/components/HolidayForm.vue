<template>
  <v-form
    v-model="isValid"
    @update:model-value="updatedValidity"
    :disabled="disabled"
  >
    <v-text-field
      v-model="modelValue.name"
      :label="t('domain.holidays.public.name')"
      :rules="[maxNameLength, minTwoChars]"
      variant="outlined"
      autofocus
    />
    <v-card
      flat
      border
      class="border-opacity-25"
    >
      <v-card-title class="v-label font-weight-regular">
        {{ t("domain.holidays.public.date") }}
      </v-card-title>
      <v-row
        justify="start"
        class="px-4"
      >
        <v-col
          cols="12"
          sm="6"
          class="pr-10"
        >
          <v-date-input
            ref="startDateInput"
            v-model="modelValue.startDate"
            :label="
              isPublic ? undefined : t('domain.holidays.school.startDate')
            "
            :rules="[datePicked, isStartDateBeforeEndDate]"
            :allowed-dates="isStartDateAllowed"
            :prepend-icon="mdiCalendar"
            variant="underlined"
            @update:model-value="updateAndValidateEndDate"
          />
        </v-col>
        <v-col
          cols="12"
          sm="6"
          class="pr-10"
          v-if="!isPublic"
        >
          <v-date-input
            ref="endDateInput"
            v-model="modelValue.endDate"
            :label="t('domain.holidays.school.endDate')"
            :rules="[datePicked, isEndDateAfterStartDate]"
            :allowed-dates="isEndDateAllowed"
            :prepend-icon="mdiCalendar"
            variant="underlined"
            @update:model-value="validateStartDate"
          ></v-date-input>
        </v-col>
      </v-row>
    </v-card>
  </v-form>
</template>

<script setup lang="ts">
import type { HolidayRequestDTO } from "@/api/raumreservierung-backend";
import type { VDateInput } from "vuetify/labs/VDateInput";

import { mdiCalendar } from "@mdi/js";
import { ref, useTemplateRef } from "vue";
import { useI18n } from "vue-i18n";

const MIN_NAME_LENGTH = 2;
const MAX_NAME_LENGTH = 100;

const modelValue = defineModel<HolidayRequestDTO>({ required: true });
const isValid = ref<boolean | null>(false);

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

const datePicked = (value: Date) =>
  value != undefined || t("common.rules.noDateSelected");

const updatedValidity = (newIsValid: boolean | null) => {
  isValid.value = newIsValid;
  emit("isValid", newIsValid);
};

const isEndDateAllowed = (date: unknown) => {
  if (!(date instanceof Date)) {
    return false;
  }
  return !modelValue.value.startDate || date > modelValue.value.startDate;
};

const isStartDateAllowed = (date: unknown) => {
  if (isPublic) {
    return true;
  }
  if (!(date instanceof Date)) {
    return false;
  }
  return !modelValue.value.endDate || date < modelValue.value.endDate;
};

const isStartDateBeforeEndDate = (startDate: Date) => {
  if (isPublic) {
    return true;
  } else {
    return (
      !modelValue.value.endDate ||
      startDate < modelValue.value.endDate ||
      "Beginn liegt nach Ende."
    );
  }
};

const isEndDateAfterStartDate = (endDate: Date) =>
  !modelValue.value.startDate ||
  endDate > modelValue.value.startDate ||
  "Ende liegt vor Beginn.";

const startDateInput =
  useTemplateRef<InstanceType<typeof VDateInput>>("startDateInput");

const validateStartDate = () => startDateInput.value?.validate();

const endDateInput =
  useTemplateRef<InstanceType<typeof VDateInput>>("endDateInput");

const validateEndDate = () => endDateInput.value?.validate();

const updateEndDateIfPublic = () => {
  if (isPublic) {
    modelValue.value.endDate = modelValue.value.startDate;
  }
};

const updateAndValidateEndDate = () => {
  updateEndDateIfPublic();
  validateEndDate();
};
</script>

<style scoped></style>
