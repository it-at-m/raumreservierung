<template>
  <v-card
    flat
    border
    :loading="loading"
    class="border-opacity-25"
  >
    <template #title>
      <slot name="title">
        {{ title }}
      </slot>
    </template>
    <template #subtitle>
      <slot name="subtitle">
        {{ subtitle }}
      </slot>
    </template>
    <template #text>
      <slot name="text" />
    </template>
  </v-card>
</template>

<script setup lang="ts">
defineProps<{
  title?: string;
  subtitle?: string;
  loading?: boolean;
}>();
</script>

<style scoped></style>
