<template>
  <v-chip
    v-if="status"
    :variant="variant"
    :color="variant !== 'text' ? config.color : undefined"
    :class="chipClass"
    :density="density"
    :text="config.text"
  >
    <template #prepend>
      <v-icon
        :icon="config.icon"
        :color="config.color"
        class="mr-2"
      ></v-icon>
    </template>
  </v-chip>
</template>

<script setup lang="ts">
import { computed } from "vue";

import { useBookingStatusConfig } from "@/composables/useBookingStatus";

const {
  variant = "outlined",
  density = "default",
  status,
} = defineProps<{
  status: string | undefined;
  variant?: "text" | "outlined";
  density?: "default" | "compact";
}>();

const { getStatusConfig } = useBookingStatusConfig();

const config = computed(() => getStatusConfig(status));

const chipClass = computed(() => (variant === "text" ? "ml-n3" : "pl-2"));
</script>
