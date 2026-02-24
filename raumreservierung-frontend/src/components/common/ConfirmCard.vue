<template>
  <v-card
    :title="title"
    :subtitle="subtitle"
    :text="text"
  >
    <template #actions>
      <slot name="actions">
        <base-button
          secondary
          class="mr-4"
          @click="emit('cancel')"
        >
          <template #default> {{ computedCancelText }} </template>
          <template #prepend>
            <v-icon :icon="mdiClose" />
          </template>
        </base-button>
        <base-button @click="emit('confirm')">
          <template #default> {{ computedConfirmText }} </template>
          <template #append>
            <v-icon :icon="mdiContentSaveOutline" />
          </template>
        </base-button>
      </slot>
    </template>
  </v-card>
</template>

<script setup lang="ts">
import { mdiClose, mdiContentSaveOutline } from "@mdi/js";
import { computed } from "vue";
import { useI18n } from "vue-i18n";

import BaseButton from "@/components/common/buttons/BaseButton.vue";

const { t } = useI18n();

const { confirmText, cancelText } = defineProps<{
  title: string;
  subtitle?: string;
  text?: string;
  confirmText?: string;
  cancelText?: string;
}>();

const computedConfirmText = computed(() =>
  confirmText ? confirmText : t("common.delete")
);

const computedCancelText = computed(() =>
  cancelText ? cancelText : t("common.cancel")
);

const emit = defineEmits<{
  confirm: [];
  cancel: [];
}>();
</script>

<style scoped></style>
