<template>
  <v-card height="250">
    <template #default>
      <div class="w-100 h-75 d-flex justify-center align-center pt-4">
        <v-icon
          size="70"
          color="accent"
          :icon="mdiImage"
        />
      </div>
    </template>
    <template #actions>
      <v-card-title>{{ room.name }} </v-card-title>
    </template>
  </v-card>
</template>

<script setup lang="ts">
import type { RoomListResponseDTO } from "@/api/raumreservierung-backend";

import { mdiImage } from "@mdi/js";

defineProps<{
  room: RoomListResponseDTO;
}>();
</script>
