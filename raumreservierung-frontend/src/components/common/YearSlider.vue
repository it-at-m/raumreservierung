<template>
  <v-sheet
    class="mx-auto py-2"
    max-width="400"
  >
    <v-slide-group
      v-model="selectedYear"
      show-arrows
      center-active
      mandatory
    >
      <v-slide-group-item
        v-for="year in years"
        :key="year"
        :value="year"
        v-slot="{ isSelected, toggle }"
      >
        <v-btn
          width="100"
          variant="text"
          :border="isSelected ? 'b-md tertiary opacity-75 ' : false"
          rounded="0"
          :color="isSelected ? 'tertiary' : undefined"
          @click="toggle"
        >
          {{ year }}
        </v-btn>
      </v-slide-group-item>
    </v-slide-group>
  </v-sheet>
</template>
<script setup lang="ts">
import { computed } from "vue";

const { startYear = 2000, endYear = 2100 } = defineProps<{
  startYear?: number;
  endYear?: number;
}>();

const selectedYear = defineModel<number>({ default: new Date().getFullYear() });

const years = computed(() =>
  Array.from({ length: endYear - startYear + 1 }, (_, i) => startYear + i)
);
</script>

<style scoped></style>
