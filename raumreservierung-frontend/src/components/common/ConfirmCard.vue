<template>
  <v-card
    :title="title"
    :subtitle="subtitle"
    :loading="loading"
  >
    <template #text>
      <slot name="text">
        {{ text }}
      </slot>
    </template>
    <template #actions>
      <div class="mb-4 mr-4">
        <slot
          name="cancel"
          :props="{ onClick: cancel }"
        >
          <base-button
            secondary
            class="mr-4"
            @click="cancel"
            :prepend-icon="mdiClose"
            :text="t('common.cancel')"
          />
        </slot>
        <slot
          name="confirm"
          :props="{ onClick: confirm }"
        />
      </div>
    </template>
  </v-card>
</template>

<script setup lang="ts">
import { mdiClose } from "@mdi/js";
import { useI18n } from "vue-i18n";

import BaseButton from "@/components/common/buttons/BaseButton.vue";

const { t } = useI18n();

const { loading = false } = defineProps<{
  title: string;
  subtitle?: string;
  text?: string;
  loading?: boolean;
}>();

const emit = defineEmits<{
  confirm: [];
  cancel: [];
}>();

const confirm = () => {
  emit("confirm");
};

const cancel = () => emit("cancel");
</script>

<style scoped></style>
