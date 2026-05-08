<template>
  <base-view header-text="Raumdetails">
    <template #headerPrepend>
      <v-icon
        size="30"
        :icon="mdiArrowLeft"
        @click="router.back()"
      />
    </template>
    <template #headerActions>
      <base-button
        text="Bearbeiten"
        :append-icon="mdiPencil"
        @click="
          router.push({
            name: ROUTES.ROOMS_EDIT,
            params: { id },
          })
        "
      />
    </template>

    <template #default>
      <v-row>
        <v-col
          cols="12"
          md="6"
        >
          <v-skeleton-loader
            :loading="getRoomLoading"
            type="article"
          >
            <v-responsive>
              <v-card
                flat
                :title="roomData?.name"
                :subtitle="`${roomData?.number} - ${roomData?.location}`"
                :text="roomData?.locationDescription"
              />
            </v-responsive>
          </v-skeleton-loader>
        </v-col>
        <v-col
          cols="12"
          md="6"
        >
          <v-skeleton-loader
            :loading="getRoomLoading"
            class="mb-4"
            type="image"
          >
            <v-responsive>
              <v-card
                class="mb-4 d-flex align-center justify-center bg-grey-lighten-4"
                height="250"
                flat
                border
              >
                <div class="text-center text-grey">
                  <v-icon
                    :icon="mdiImage"
                    size="48"
                    class="mb-2"
                  />
                </div>
              </v-card>
            </v-responsive>
          </v-skeleton-loader>
        </v-col>
        <v-col
          cols="12"
          md="6"
        >
          <rooms-details-card
            title="Ansprechperson"
            :icon="mdiAccount"
            :loading="getRoomLoading"
          >
            <p class="text-grey-darken-4">
              {{ roomData?.contactPerson?.title }}
              {{ roomData?.contactPerson?.firstName }}
              {{ roomData?.contactPerson?.lastName }}
            </p>
            <p class="text-grey-darken-4">
              Tel: {{ roomData?.contactPerson?.telefonNumber }}
            </p>
            <p class="text-grey-darken-4">
              E-Mail: {{ roomData?.contactPerson?.email }}
            </p>
          </rooms-details-card>
        </v-col>
        <v-col
          cols="12"
          md="6"
        >
          <rooms-details-card
            title="Nutzbare Fläche"
            :icon="mdiTextureBox"
            :loading="getRoomLoading"
          >
            <p class="text-grey-darken-4">{{ roomData?.area }} qm</p>
          </rooms-details-card>
        </v-col>
        <v-col
          cols="12"
          md="6"
        >
          <rooms-details-card
            title="Kapazität"
            :icon="mdiHumanCapacityIncrease"
            :loading="getRoomLoading"
          >
            <template #subtitle>
              <p class="text-grey-darken-4">
                Maximale Kapazität: {{ roomData?.capacity }}
              </p>
            </template>
            <v-list
              class="mt-0 pt-0"
              density="compact"
            >
              <v-list-item
                v-for="(capacity, idx) in roomData?.roomSeatingCapacities"
                :key="idx"
                v-tooltip:end="capacity.seatingType.description"
                :title="capacity.seatingType.name"
                :subtitle="`${capacity.capacity} Personen`"
              />
            </v-list>
          </rooms-details-card>
        </v-col>

        <v-col
          cols="12"
          md="6"
        >
          <rooms-details-card
            title="Mögliche Ausstattung"
            :icon="mdiSofaSingleOutline"
            :loading="getRoomLoading"
          >
            <v-list
              class="mt-0 pt-0"
              density="compact"
            >
              <v-list-item
                v-for="(eq, idx) in roomData?.equipment"
                :key="idx"
                :title="eq.name"
                :subtitle="eq.description"
              />
            </v-list>
          </rooms-details-card>
        </v-col>
      </v-row>
    </template>
  </base-view>
</template>

<script setup lang="ts">
import type { RoomDetailsResponseDTO } from "@/api/raumreservierung-backend";

import {
  mdiAccount,
  mdiArrowLeft,
  mdiHumanCapacityIncrease,
  mdiImage,
  mdiPencil,
  mdiSofaSingleOutline,
  mdiTextureBox,
} from "@mdi/js";
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseView from "@/components/common/BaseView.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import RoomsDetailsCard from "@/components/rooms/RoomsDetailsCard.vue";
import { useRoomCache } from "@/composables/cache/useRoomCache.ts";
import { ROUTES } from "@/types/Routes.ts";

const router = useRouter();
const route = useRoute();

const id = computed(() => route.params.id as string | undefined);

const roomData = ref<RoomDetailsResponseDTO>();

const { call, loading: getRoomLoading } = useRoomCache();

onMounted(async () => {
  if (id.value) {
    const result = await call(id.value);

    if (result) {
      roomData.value = result as RoomDetailsResponseDTO;
    }
  }
});
</script>

<style scoped></style>
