<template>
  <base-view>
    <template #header>
      <v-row justify="space-between">
        <v-col cols="auto">
          <div class="d-flex align-center">
            <v-icon
              class="mr-5 ml-2"
              size="30"
              :icon="mdiArrowLeft"
              @click="router.back()"
            />
            <p>Raumdetails</p>
          </div>
        </v-col>
        <v-col cols="auto">
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
        </v-col>
      </v-row>
    </template>
    <template #default>
      <v-row>
        <v-col cols="4">
          <v-skeleton-loader
            elevation="1"
            :loading="getRoomLoading"
            class="mb-4"
            type="article"
          >
            <v-responsive>
              <v-card
                class="mb-4"
                flat
                :title="getRoomData?.name"
                :subtitle="`${getRoomData?.number} - ${getRoomData?.location}`"
                :text="getRoomData?.locationDescription"
              />
            </v-responsive>
          </v-skeleton-loader>

          <rooms-details-card
            title="Kapazität"
            :icon="mdiHumanCapacityIncrease"
            :loading="getRoomLoading"
          >
            <template #subtitle>
              <p class="text-grey-darken-4">
                Maximale Kapazität: {{ getRoomData?.capacity }}
              </p>
            </template>
            <v-list
              class="mt-0 pt-0"
              density="compact"
            >
              <v-list-item
                v-for="(capacity, idx) in getRoomData?.roomSeatingCapacities"
                :key="idx"
                v-tooltip:end="capacity.seatingType.description"
                :title="capacity.seatingType.name"
                :subtitle="`${capacity.capacity} Personen`"
              />
            </v-list>
          </rooms-details-card>

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
                v-for="(eq, idx) in getRoomData?.equipment"
                :key="idx"
                :title="eq.name"
                :subtitle="eq.description"
              />
            </v-list>
          </rooms-details-card>

          <rooms-details-card
            title="Ansprechperson"
            :icon="mdiAccount"
            :loading="getRoomLoading"
          >
            <p class="text-grey-darken-4">
              {{ getRoomData?.contactPerson?.title }}
              {{ getRoomData?.contactPerson?.firstName }}
              {{ getRoomData?.contactPerson?.lastName }}
            </p>
            <p class="text-grey-darken-4">
              Tel: {{ getRoomData?.contactPerson?.telefonNumber }}
            </p>
            <p class="text-grey-darken-4">
              E-Mail: {{ getRoomData?.contactPerson?.email }}
            </p>
          </rooms-details-card>

          <rooms-details-card
            title="Nutzbare Fläche"
            :icon="mdiTextureBox"
            :loading="getRoomLoading"
          >
            <p class="text-grey-darken-4">{{ getRoomData?.area }} qm</p>
          </rooms-details-card>
        </v-col>
      </v-row>
    </template>
  </base-view>
</template>

<script setup lang="ts">
import {
  mdiAccount,
  mdiArrowLeft,
  mdiHumanCapacityIncrease,
  mdiMail,
  mdiPencil,
  mdiPhone,
  mdiSofaSingleOutline,
  mdiTextureBox,
} from "@mdi/js";
import { computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseView from "@/components/common/BaseView.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import RoomsDetailsCard from "@/components/rooms/RoomsDetailsCard.vue";
import { useGetRoom } from "@/composables/api/useRoomsApi.ts";
import { ROUTES } from "@/types/Routes.ts";

const router = useRouter();
const route = useRoute();

const id = computed(() => route.params.id as string | undefined);

const {
  call: getRoom,
  data: getRoomData,
  loading: getRoomLoading,
} = useGetRoom();

onMounted(async () => {
  if (id.value) {
    await getRoom({ roomId: id.value });
  } else {
    console.log("ID was empty");
  }
});
</script>

<style scoped></style>
