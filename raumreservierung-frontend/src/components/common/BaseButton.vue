<template>
  <v-btn
    :color="secondary ? '' : 'secondary'"
    :variant="secondary ? 'outlined' : 'flat'"
    :disabled="disabled"
    @click="emit('click')"
  >
    <template #default>
      <slot name="default" />
    </template>
    <template #append>
      <slot name="append" />
    </template>
    <template #prepend>
      <slot name="prepend" />
    </template>
  </v-btn>
</template>

<script setup lang="ts">
const { secondary = false } = defineProps<{
  secondary?: boolean;
  disabled?: boolean;
}>();

defineSlots<{
  default(): void;
  append(): void;
  prepend(): void;
}>();

const emit = defineEmits<(e: "click") => void>();
</script>

<style scoped></style>
