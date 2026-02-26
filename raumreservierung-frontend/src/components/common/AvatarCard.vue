<template>
  <v-card
    variant="flat"
    :loading="isLoading"
  >
    <template #prepend>
      <ad2-image-avatar username="" />
    </template>
    <template #title>
      <span class="text-capitalize">
        {{ userStore.user?.name }}
      </span>
    </template>
    <template #subtitle>
      <span class="text-capitalize">
        {{ userStore.user?.user_roles.toString() }}</span
      >
    </template>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from "vue";

import Ad2ImageAvatar from "@/components/common/Ad2ImageAvatar.vue";
import { useUserStore } from "@/stores/user.ts";

const userStore = useUserStore();

const isLoading = computed(() => userStore.user == null);
</script>

<style scoped>
.text-capitalize {
  text-transform: capitalize;
}
</style>
