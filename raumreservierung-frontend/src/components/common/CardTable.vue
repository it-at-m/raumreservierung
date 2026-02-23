<template>
  <v-card
    class="mt-4 pt-2"
    :loading="loading"
  >
    <template #title>
      <v-row align-content="center">
        <v-col class="d-flex align-center justify-start">
          <div class="text-h6 pl-2">Überschrift</div>
        </v-col>
        <v-col class="d-flex align-center justify-end">
          <slot name="action" />
        </v-col>
      </v-row>
      <v-row class="mt-2">
        <v-col>
          <v-divider />
        </v-col>
      </v-row>
    </template>
    <template #text>
      <v-data-table
        :headers="headers"
        :items="items"
        hide-default-footer
        items-per-page="-1"
      >
        <template
          v-for="(_, slotName) in $slots"
          v-slot:[slotName]="slotProps"
        >
          <slot
            :name="slotName"
            v-bind="slotProps || {}"
          />
        </template>
      </v-data-table>
    </template>
  </v-card>
</template>

<script setup lang="ts" generic="T extends Record<string, unknown>">
export interface TableHeader<T> {
  title: string;
  value: Extract<keyof T, string> | (string & {});
  align?: "start" | "center" | "end";
  sortable?: boolean;
  width?: string | number;
}

defineProps<{
  items: readonly T[];
  headers: TableHeader<T>[];
  loading?: boolean;
}>();
</script>

<style scoped></style>
