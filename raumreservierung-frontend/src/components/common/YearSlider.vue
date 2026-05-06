<template>
  <v-sheet
    class="mx-auto py-2"
    max-width="600"
  >
    <v-slide-group
      v-model="selectedYear"
      color="tertiary"
      show-arrows
      mandatory
      center-active
    >
      <v-slide-group-item
        v-for="year in years"
        :key="year"
        v-slot="{ select, selectedClass }"
        :value="year"
        selected-class="border-b-md border-tertiary text-tertiary"
      >
        <v-btn
          class="px-8"
          variant="flat"
          :class="[selectedClass]"
          rounded="0"
          @click="select"
        >
          {{ year }}
        </v-btn>
      </v-slide-group-item>
    </v-slide-group>
  </v-sheet>
</template>
<script setup lang="ts">
import { computed } from "vue";

const { startYear, endYear } = defineProps<{
  startYear: number;
  endYear: number;
}>();

const selectedYear = defineModel<number>({ default: new Date().getFullYear() });

const years = computed(() =>
  Array.from({ length: endYear - startYear + 1 }, (_, i) => startYear + i)
);
</script>

<style scoped></style>
