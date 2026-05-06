<template>
  <v-card
    flat
    border
    :loading="loading"
    class="border-opacity-25"
  >
    <template
      #title
      v-if="slots.title"
    >
      <slot name="title">
        {{ title }}
      </slot>
    </template>
    <template
      #subtitle
      v-if="subtitle || slots.subtitle"
    >
      <slot name="subtitle">
        {{ subtitle }}
      </slot>
    </template>
    <template
      #text
      v-if="slots.text"
    >
      <slot name="text" />
    </template>
    <template
      #actions
      v-if="slots.actions"
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
