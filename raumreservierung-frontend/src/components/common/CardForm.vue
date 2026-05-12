<template>
  <v-card
    flat
    border
    :loading="loading"
    class="border-opacity-25"
  >
    <template
      v-if="slots.title"
      #title
    >
      <slot name="title">
        {{ title }}
      </slot>
    </template>
    <template
      v-if="subtitle || slots.subtitle"
      #subtitle
    >
      <slot name="subtitle">
        {{ subtitle }}
      </slot>
    </template>
    <template
      v-if="slots.text"
      #text
    >
      <slot name="text" />
    </template>
    <template
      v-if="slots.actions"
      #actions
    >
      <slot name="actions" />
    </template>
  </v-card>
</template>

<script setup lang="ts">
defineProps<{
  title?: string;
  subtitle?: string;
  loading?: boolean;
}>();

const slots = defineSlots<{
  title(): void;
  text(): void;
  subtitle(): void;
  actions(): void;
}>();
</script>

<style scoped></style>
