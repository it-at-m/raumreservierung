<template>
  <v-card
    :title="title"
    :subtitle="subtitle"
    :loading="loading"
  >
    <template #text>
      <slot
        name="text"
        :disabled="loading"
      >
        {{ text }}
        <v-text-field
          v-model="inputText"
          :disabled="loading"
          label="Begründung eingeben"
          variant="outlined"
          hide-details
          @keydown.enter="confirm"
        />
      </slot>
    </template>
    <template #actions>
      <div class="mb-4 mr-4">
        <slot
          name="cancel"
          :props="{ onClick: cancel }"
          :disabled="loading"
        >
          <base-button
            secondary
            class="mr-4"
            :disabled="loading"
            :prepend-icon="mdiClose"
            :text="t('common.cancel')"
            @click="cancel"
          />
        </slot>
        <slot
          name="confirm"
          :props="{ onClick: confirm }"
          :disabled="loading"
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

const inputText = defineModel<string>({ default: "" });

const { loading = false } = defineProps<{
  title: string;
  subtitle?: string;
  text?: string;
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
