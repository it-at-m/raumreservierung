<template>
  <v-list>
    <navigation-drawer-item
      v-for="navEntry in navEntries"
      :key="navEntry.textKey"
      :nav-entry="navEntry"
    />
  </v-list>
</template>

<script setup lang="ts">
import NavigationDrawerItem from "@/components/common/NavigationDrawerItem.vue";
import { useNavEntries } from "@/composables/useNavEntries.ts";

const navEntries = useNavEntries();
</script>

<style scoped></style>
