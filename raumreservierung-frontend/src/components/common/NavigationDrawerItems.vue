<template>
  <v-list>
    <v-list-item :to="{ name: ROUTES_GETSTARTED }">
      <v-list-item-title>
        {{ t("navigationDrawer.myBookings") }}
      </v-list-item-title>
    </v-list-item>
    <v-list-item :to="{ name: ROUTES_GETSTARTED }">
      <v-list-item-title>
        {{ t("navigationDrawer.viewRoomsAndDetails") }}
      </v-list-item-title>
    </v-list-item>
    <v-list-item :to="{ name: ROUTES_GETSTARTED }">
      <v-list-item-title>
        {{ t("navigationDrawer.help") }}
      </v-list-item-title>
    </v-list-item>
  </v-list>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";

import { ROUTES_GETSTARTED } from "@/constants.ts";

const { t } = useI18n();
</script>

<style scoped></style>
