<template>
  <v-dialog v-if="visible">
    <template #activator="{ props }">
      <base-button
        v-bind="props"
        :append-icon="icon"
        :text="text"
      />
    </template>
    <template #default="{ isActive }">
      <component
        :is="content"
        @close="isActive.value = false"
      />
    </template>
  </v-dialog>
</template>

<script setup lang="ts">
import type { Component } from "vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";

defineProps<{
  icon: string;
  text: string;
  content: Component;
  visible: boolean;
}>();
</script>

<style scoped></style>
