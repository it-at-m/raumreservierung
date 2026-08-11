<template>
  <div class="d-flex align-center">
    <v-icon
      :icon="icon"
      class="mr-2"
    />
    <span>{{ text }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  icon?: string;
  text?: string;
}>();
</script>

<style scoped></style>
