<script setup lang="ts">
import type { HolidayRequestDTO } from "@/api/raumreservierung-backend";
import type { VDateInput } from "vuetify/labs/VDateInput";

import { mdiCalendar } from "@mdi/js";
import { ref } from "vue";
import { useI18n } from "vue-i18n";

const MIN_NAME_LENGTH = 2;
const MAX_NAME_LENGTH = 100;

const COL_SIZE = 12;
const SM_SIZE = 6;
const MD_SIZE = 3;

const modelValue = defineModel<HolidayRequestDTO>({ required: true });
const isValid = ref<boolean | null>(false);

const { isPublic, disabled } = defineProps<{
  isPublic: boolean;
  disabled?: boolean;
}>();

const emit = defineEmits<{
  isValid: [value: boolean | null];
}>();

const { t } = useI18n();

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
  if (!(date instanceof Date)) return false;
  return modelValue.value.startDate ? date > modelValue.value.startDate : false;
};

const isStartDateAllowed = (date: unknown) => {
  if (isPublic) return true;
  if (!(date instanceof Date)) return false;
  return modelValue.value.endDate ? date < modelValue.value.endDate : true;
};

const updateEndDateIfPublic = () => {
  if (isPublic) modelValue.value.endDate = modelValue.value.startDate;
};
</script>

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
    ></v-text-field>
    <v-card
      variant="outlined"
      style="border: 1px solid #ababab"
    >
      <v-card-title class="v-label font-weight-regular">
        {{ t("domain.holidays.public.date") }}
      </v-card-title>
      <v-row class="px-2">
        <v-col
          :cols="COL_SIZE"
          :sm="SM_SIZE"
          :md="MD_SIZE"
          class="py-0 my-0"
        >
          <v-date-input
            v-model="modelValue.startDate"
            :label="
              isPublic ? undefined : t('domain.holidays.school.startDate')
            "
            :rules="[datePicked]"
            :allowed-dates="isStartDateAllowed"
            :prepend-icon="mdiCalendar"
            variant="underlined"
            @update:model-value="updateEndDateIfPublic"
          ></v-date-input>
        </v-col>
        <v-col
          :cols="COL_SIZE"
          :sm="SM_SIZE"
          :md="MD_SIZE"
          class="hidden-xs"
        ></v-col>
        <v-col
          :cols="COL_SIZE"
          :sm="SM_SIZE"
          :md="MD_SIZE"
          class="py-0 my-0"
          v-if="!isPublic"
        >
          <v-date-input
            v-model="modelValue.endDate"
            ref="endDateInput"
            :label="t('domain.holidays.school.endDate')"
            :rules="[datePicked]"
            :allowed-dates="isEndDateAllowed"
            :prepend-icon="mdiCalendar"
            variant="underlined"
          ></v-date-input>
        </v-col>
        <v-col
          :cols="COL_SIZE"
          :sm="SM_SIZE"
          :md="MD_SIZE"
          class="hidden-xs"
        ></v-col>
      </v-row>
    </v-card>
  </v-form>
</template>

<style scoped></style>
