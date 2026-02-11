<template>
  <v-card
    variant="flat"
    :loading="isLoading"
  >
    <template #prepend>
      <ad2-image-avatar username="" />
    </template>
    <template #title> {{ userStore.getUser?.username }} </template>
    <template #subtitle>
      {{ userStore.getUser?.user_roles.toString() }}
    </template>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from "vue";

import Ad2ImageAvatar from "@/components/common/Ad2ImageAvatar.vue";
import { useUserStore } from "@/stores/user.ts";

const userStore = useUserStore();

const isLoading = computed(() => userStore.getUser == null);
</script>

<style scoped></style>
