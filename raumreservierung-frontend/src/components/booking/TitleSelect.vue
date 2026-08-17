<template>
  <v-text-field
    v-model="modelValue"
    :label="t('domain.booking.bookingTitle')"
    color="accent"
    variant="outlined"
    density="compact"
    clearable
    hide-details
    :prepend-inner-icon="mdiTextBoxSearchOutline"
    @update:model-value="onInput"
    @click:clear="onInput(undefined)"
  />
</template>

<script setup lang="ts">
import { mdiTextBoxSearchOutline } from "@mdi/js";
import { useDebounceFn } from "@vueuse/core";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const modelValue = defineModel<string>();

const onInput = useDebounceFn((value: string | undefined) => {
  modelValue.value = value || undefined;
}, 500);
</script>
