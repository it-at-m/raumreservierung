<template>
  <v-chip
    v-if="status"
    :variant="variant"
    :color="variant !== 'text' ? config.color : undefined"
    :class="{ 'ml-n3': variant === 'text', 'pl-2': variant !== 'text' }"
    :text="config.text"
  >
    <template #prepend>
      <v-icon
        :icon="config.icon"
        :color="config.color"
        class="mr-2"
      />
    </template>
  </v-chip>
</template>

<script setup lang="ts">
import { useBookingStatusConfig } from "@/composables/useBookingStatus";

const { variant = "outlined", status } = defineProps<{
  status: string | undefined;
  variant?: "text" | "outlined";
}>();

const { config } = useBookingStatusConfig(() => status);
</script>
