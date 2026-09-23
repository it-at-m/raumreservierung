<template>
  <v-skeleton-loader
    elevation="1"
    :loading="loading"
    type="image"
  >
    <v-responsive>
      <v-card
        color="secondary"
        variant="tonal"
      >
        <template #title>
          <v-icon
            class="my-2"
            :icon="icon"
            color="primary"
          />
          <p class="text-high-emphasis my-0">{{ title }}</p>
        </template>

        <template
          v-if="subtitle"
          #subtitle
        >
          <p class="text-high-emphasis my-0">{{ subtitle }}</p>
        </template>

        <template
          v-if="slots.default"
          #text
        >
          <slot name="default" />
        </template>
      </v-card>
    </v-responsive>
  </v-skeleton-loader>
</template>

<script setup lang="ts">
import type { VNode } from "vue";

defineProps<{
  title: string;
  subtitle?: string;
  icon: string;
  loading: boolean;
}>();

const slots = defineSlots<{
  default(): VNode[];
}>();
</script>
