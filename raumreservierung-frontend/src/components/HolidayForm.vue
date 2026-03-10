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
            :label="isPublic ? '' : t('domain.holidays.school.startDate')"
            :rules="[datePicked, isStartDateBeforeEndDate]"
            :allowed-dates="isStartDateAllowed"
            :prepend-icon="mdiCalendar"
            variant="underlined"
            @update:model-value="validateDate('endDate')"
          />
        </v-col>
        <v-col
          v-if="!isPublic"
          cols="12"
          sm="6"
          class="pr-10"
        >
          <v-date-input
            ref="endDateInput"
            v-model="modelValue.endDate"
            :label="t('domain.holidays.school.endDate')"
            :rules="[datePicked, isEndDateAfterStartDate]"
            :allowed-dates="isEndDateAllowed"
            :prepend-icon="mdiCalendar"
            variant="underlined"
            @update:model-value="validateDate('startDate')"
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

const datePicked = (value: Date) =>
  value != undefined || t("common.rules.noDateSelected");

const updatedValidity = (newIsValid: boolean | null) => {
  isValid.value = newIsValid;
  emit("isValid", newIsValid);
};

const isEndDateAllowed = (endDate: unknown) =>
  endDate instanceof Date &&
  (!modelValue.value.startDate || endDate > modelValue.value.startDate);

const isStartDateAllowed = (startDate: unknown) =>
  startDate instanceof Date &&
  (isPublic ||
    !modelValue.value.endDate ||
    startDate < modelValue.value.endDate);

const isStartDateBeforeEndDate = (startDate: Date) =>
  isPublic ||
  !modelValue.value.endDate ||
  startDate < modelValue.value.endDate ||
  t("common.rules.startAfterEnd");

const isEndDateAfterStartDate = (endDate: Date) =>
  !modelValue.value.startDate ||
  endDate > modelValue.value.startDate ||
  t("common.rules.endBeforeStart");

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
