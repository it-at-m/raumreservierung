<template>
  <confirm-card
    :title="title"
    :subtitle="subtitle"
    :loading="loading"
    @cancel="cancel"
  >
    <template #text="{ disabled }">
      <v-text-field
        v-model="inputText"
        :disabled="disabled"
        :label="t('domain.booking.statusChange.enterReason')"
        variant="outlined"
        hide-details
      />
    </template>

    <template #confirm="{ disabled }">
      <base-button
        :disabled="disabled"
        :text="t('common.save')"
        :append-icon="mdiContentSaveOutline"
        @click="confirm"
      />
    </template>
  </confirm-card>
</template>

<script setup lang="ts">
import { mdiContentSaveOutline } from "@mdi/js";
import { useI18n } from "vue-i18n";

import BaseButton from "@/components/common/buttons/BaseButton.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";

const { t } = useI18n();

const inputText = defineModel<string>({ default: "" });

const { loading = false } = defineProps<{
  title: string;
  subtitle?: string;
  loading?: boolean;
}>();

const emit = defineEmits<{
  confirm: [text: string];
  cancel: [];
}>();

const confirm = () => {
  emit("confirm", inputText.value);
  inputText.value = "";
};

const cancel = () => {
  inputText.value = "";
  emit("cancel");
};
</script>

<style scoped></style>
