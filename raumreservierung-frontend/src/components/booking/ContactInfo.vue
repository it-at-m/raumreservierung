<template>
  <div>
    <div
      v-if="name"
      class="text-medium-emphasis d-flex align-center"
    >
      <v-icon
        :icon="mdiCardAccountDetails"
        size="small"
        class="mr-2"
      />
      {{ name }}
    </div>
    <div
      v-if="email"
      class="text-medium-emphasis d-flex align-center"
    >
      <v-icon
        :icon="mdiEmail"
        size="small"
        class="mr-2"
      />
      <a
        :href="`mailto:${email}`"
        class="text-decoration-none text-medium-emphasis"
      >
        {{ email }}
      </a>
    </div>
    <div
      v-if="phone"
      class="text-medium-emphasis d-flex align-center"
    >
      <v-icon
        :icon="mdiPhone"
        size="small"
        class="mr-2"
      />
      {{ phone }}
    </div>
    <div
      v-if="organisation"
      class="text-medium-emphasis d-flex align-center"
    >
      <v-icon
        :icon="mdiDomain"
        size="small"
        class="mr-2"
      />
      {{ organisation }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { mdiCardAccountDetails, mdiDomain, mdiEmail, mdiPhone } from "@mdi/js";

defineProps<{
  name?: string | null;
  organisation?: string | null;
  email?: string | null;
  phone?: string | null;
}>();
</script>
