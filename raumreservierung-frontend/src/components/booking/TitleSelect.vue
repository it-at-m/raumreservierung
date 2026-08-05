<template>
  <v-text-field
    v-model="localValue"
    label="Veranstaltungstitel"
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
import { ref } from "vue";

const modelValue = defineModel<string>();

const localValue = ref(modelValue.value);

const onInput = useDebounceFn((value: string | undefined) => {
  modelValue.value = value || undefined;
}, 500);
</script>
