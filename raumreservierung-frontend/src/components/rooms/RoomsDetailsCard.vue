<template>
  <v-skeleton-loader
    elevation="1"
    :loading="loading"
    type="card"
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
          <p class="text-grey-darken-4 my-0">{{ title }}</p>
        </template>

        <template
          #subtitle
          v-if="subtitle"
        >
          <p class="text-grey-darken-4 my-0">{{ subtitle }}</p>
        </template>

        <template
          #text
          v-if="slots.default"
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
