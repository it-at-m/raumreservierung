<template>
  <base-view header-text="Räume und Details">
    <template #headerActions>
      <base-button
        v-if="canAddNewRoom"
        text="Hinzufügen"
        :append-icon="mdiPlus"
        @click="
          router.push({
            name: ROUTES.ROOMS_CREATE,
          })
        "
      />
    </template>
    <template #default>
      <p class="mb-4">
        {{ t("views.roomListView.bookWORoomMsg") }}
      </p>
      <v-row>
        <template v-if="getAllRoomLoading">
          <v-col
            v-for="el in 5"
            :key="el"
            cols="12"
            sm="6"
            md="4"
            xxl="3"
          >
            <v-skeleton-loader
              type="card"
              class="ma-4"
              elevation="1"
            />
          </v-col>
        </template>

        <template v-else>
          <v-col
            v-for="room in getAllRoomsData"
            :key="room.id"
            cols="12"
            sm="6"
            md="4"
            xxl="3"
          >
            <router-link
              :to="{
                name: ROUTES.ROOMS_DETAILS,
                params: { id: room.id },
              }"
              class="text-decoration-none"
            >
              <room-thumbnail
                :room="room"
                class="ma-4"
              />
            </router-link>
          </v-col>
        </template>
      </v-row>
    </template>
  </base-view>
</template>

<script setup lang="ts">
import { mdiPlus } from "@mdi/js";
import { onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

import BaseView from "@/components/common/BaseView.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import RoomThumbnail from "@/components/rooms/RoomThumbnail.vue";
import { useGetAllRooms } from "@/composables/api/useRoomsApi.ts";
import { useIsPrivileged } from "@/composables/useIsPrivileged.ts";
import { ROUTES } from "@/types/Routes.ts";

const { t } = useI18n();
const {
  call: getAllRooms,
  data: getAllRoomsData,
  loading: getAllRoomLoading,
} = useGetAllRooms();

const router = useRouter();

const canAddNewRoom = useIsPrivileged("rooms:write");

onMounted(async () => await getAllRooms());
</script>

<style scoped></style>
