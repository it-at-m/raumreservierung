<template>
  <div class="text-h4">
    <slot name="header">
      {{ headerText }}
    </slot>
  </div>
  <v-divider class="my-4" />
  <slot />
</template>

<script setup lang="ts">
defineProps<{
  headerText?: string;
}>();
</script>

<style scoped></style>
