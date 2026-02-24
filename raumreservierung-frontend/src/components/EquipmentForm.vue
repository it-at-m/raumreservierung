<template>
  <v-card
    :title="computedTitle"
    :loading="loading"
  >
    <template #text>
      <v-form
        v-model="isValid"
        :disabled="loading"
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
    <template #actions>
      <div class="mr-4 mb-4">
        <base-button
          secondary
          class="mr-4"
          @click="emit('cancel')"
          :disabled="loading"
        >
          <template #default> {{ t("common.cancel") }} </template>
          <template #prepend>
            <v-icon :icon="mdiClose" />
          </template>
        </base-button>
        <base-button
          :disabled="!isValid || loading"
          @click="emit('save')"
        >
          <template #default> {{ t("common.save") }} </template>
          <template #append>
            <v-icon :icon="mdiContentSaveOutline" />
          </template>
        </base-button>
      </div>
    </template>
  </v-card>
</template>

<script setup lang="ts">
import type { EquipmentResponseDto } from "@/api/raumreservierung-backend";

import { mdiClose, mdiContentSaveOutline } from "@mdi/js";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";

import BaseButton from "@/components/common/buttons/BaseButton.vue";

const MIN_NAME_LENGTH = 2;
const MAX_DESCRIPTION_LENGTH = 255;

const isValid = ref(false);

const modelValue = defineModel<EquipmentResponseDto>({ required: true });

const computedTitle = computed(() =>
  modelValue.value.id
    ? t("generics.edit", { domain: t("domain.equipment.header") })
    : t("generics.create", { domain: t("domain.equipment.header") })
);

const { t } = useI18n();

const { loading } = defineProps<{
  loading?: boolean;
}>();

const minTwoChars = (value: string) =>
  (!!value && value.length >= MIN_NAME_LENGTH) ||
  t("components.equipmentForm.rules.minCharsError", { num: MIN_NAME_LENGTH });

const maxCharLength = (value: string) =>
  value.length <= MAX_DESCRIPTION_LENGTH ||
  t("components.equipmentForm.rules.maxDescriptionError", {
    num: MAX_DESCRIPTION_LENGTH,
  });

const emit = defineEmits<(e: "cancel" | "save") => void>();
</script>

<style scoped></style>
