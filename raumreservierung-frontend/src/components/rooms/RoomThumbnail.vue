<template>
  <v-card
    height="250"
    :disabled="!room.isActive"
    :loading="pictureLoading"
  >
    <template #default>
      <div
        class="w-100 h-75 d-flex justify-center align-center bg-grey-lighten-4"
      >
        <v-img
          v-if="pictureUrl"
          :src="pictureUrl"
          class="h-100"
          cover
        />
        <v-icon
          v-else
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
import { useObjectUrl } from "@vueuse/core";
import { onMounted } from "vue";

import { useGetFile } from "@/composables/api/useFileAttachmentApi.ts";

const {
  call: getPicture,
  data: pictureData,
  loading: pictureLoading,
} = useGetFile();

const pictureUrl = useObjectUrl(pictureData);

const { room } = defineProps<{
  room: RoomListResponseDTO;
}>();

onMounted(async () => {
  if (room.pictureId) {
    await getPicture({
      fileId: room.pictureId,
    });
  }
});
</script>
