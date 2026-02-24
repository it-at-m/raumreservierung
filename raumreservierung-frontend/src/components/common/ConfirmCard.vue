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
          <template #default> {{ t("common.cancel") }} </template>
          <template #prepend>
            <v-icon :icon="mdiClose" />
          </template>
        </base-button>
        <base-button @click="emit('confirm')">
          <template #default> {{ t("common.delete") }} </template>
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
import { useI18n } from "vue-i18n";

import BaseButton from "@/components/common/buttons/BaseButton.vue";

const { t } = useI18n();

defineProps<{
  title: string;
  subtitle?: string;
  text?: string;
  noText?: string;
  yesText?: string;
}>();

const emit = defineEmits<{
  confirm: [];
  cancel: [];
}>();
</script>

<style scoped></style>
