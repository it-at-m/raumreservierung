<template>
  <div>
    <div class="text-h4">
      <slot name="header">
        <v-row justify="space-between">
          <v-col
            cols="auto"
            align-self="center"
            v-if="slot.headerPrepend"
          >
            <slot name="headerPrepend" />
          </v-col>
          <v-col align-self="center">
            <slot name="headerTitle">
              <v-card-title class="pa-0">
                {{ headerText }}
              </v-card-title>
            </slot>
          </v-col>
          <v-col
            cols="auto"
            align-self="center"
          >
            <slot name="headerActions" />
          </v-col>
        </v-row>
      </slot>
    </div>
    <v-divider class="my-4" />
    <slot />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  headerText?: string;
}>();

const slot = defineSlots<{
  default(): void;
  header(): void;
  headerPrepend(): void;
  headerTitle(): void;
  headerActions(): void;
}>();
</script>

<style scoped></style>
