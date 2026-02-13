<template>
  <v-list-item
    v-if="navEntry.kind === 'item'"
    color="tertiary"
    :value="navEntry.id"
    :to="navEntry.to"
  >
    <v-list-item-title>
      {{ t(navEntry.textKey) }}
    </v-list-item-title>
  </v-list-item>
  <v-list-group v-else>
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        :title="t(navEntry.textKey)"
      />
    </template>
    <navigation-drawer-item
      v-for="subEntry in navEntry.children"
      :key="subEntry.id"
      :nav-entry="subEntry"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import type { NavEntry } from "@/types/NavigationEntries.ts";

import { useI18n } from "vue-i18n";

const { t } = useI18n();

defineProps<{ navEntry: NavEntry }>();
</script>

<style scoped></style>
