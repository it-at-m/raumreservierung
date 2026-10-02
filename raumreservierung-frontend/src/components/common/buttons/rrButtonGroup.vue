<template>
  <v-btn-group
    :density="mobile ? 'default' : 'comfortable'"
    :divided="mobile"
    :direction="mobile ? 'vertical' : 'horizontal'"
  >
    <slot />
  </v-btn-group>
</template>

<script setup lang="ts">
import { useDisplay } from "vuetify/framework";

const { mobile } = useDisplay();
</script>

<style scoped></style>
