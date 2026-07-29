<template>
  <base-view :header-text="t('domain.room.details')">
    <template #headerPrepend>
      <v-icon
        size="30"
        :icon="mdiArrowLeft"
        @click="router.back()"
      />
    </template>
    <template #headerActions>
      <base-button
        v-if="canEditRoom"
        secondary
        :text="t('common.edit')"
        :append-icon="mdiPencil"
        @click="
          router.push({
            name: ROUTES.ROOMS_EDIT,
            params: { id },
          })
        "
      />
      <base-button
        v-if="id"
        class="ml-4"
        text="Raum buchen"
        :append-icon="mdiCalendarQuestionOutline"
        :disabled="!roomData?.isActive"
        @click="
          router.push({
            name: ROUTES.MY_BOOKINGS_CREATE,
            query: { roomId: id },
          })
        "
      />
    </template>

    <template #default>
      <v-row class="mb-4">
        <v-col>
          <v-skeleton-loader
            :loading="getRoomLoading"
            type="article"
          >
            <v-responsive>
              <v-card flat>
                <v-card-item>
                  <v-card-title>{{ roomData?.name }}</v-card-title>

                  <v-card-subtitle class="opacity-100">
                    <span class="text-medium-emphasis">
                      {{
                        t("common.format.dateRange", {
                          start: roomData?.number,
                          end: roomData?.location,
                        })
                      }}
                    </span>

                    <v-chip
                      v-if="!roomData?.isActive"
                      class="ml-2"
                      color="primary"
                      :text="
                        t('generics.inActive', {
                          domain: t('domain.room.header'),
                        })
                      "
                    />
                  </v-card-subtitle>
                </v-card-item>

                <v-card-text>
                  {{ roomData?.locationDescription }}
                </v-card-text>
              </v-card>
            </v-responsive>
          </v-skeleton-loader>
        </v-col>
        <v-col>
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
      </v-row>

      <div
        class="w-100 masonry-container"
        :class="mdAndUp ? 'masonry-cols-2' : 'masonry-cols-1'"
      >
        <div class="masonry-item w-100 d-inline-block mb-4">
          <details-card
            :title="t('domain.room.contactPerson')"
            :icon="mdiAccountOutline"
            :loading="getRoomLoading"
          >
            <v-list density="compact">
              <v-list-item class="py-0">
                <v-list-item-title>
                  {{ roomData?.contactPerson?.title }}
                  {{ roomData?.contactPerson?.firstName }}
                  {{ roomData?.contactPerson?.lastName }}
                </v-list-item-title>
              </v-list-item>
              <v-list-item
                :prepend-icon="mdiPhone"
                class="py-0"
                :title="roomData?.contactPerson?.telefonNumber"
              />
              <v-list-item
                :prepend-icon="mdiEmail"
                class="py-0"
                :title="roomData?.contactPerson?.email"
              />
            </v-list>
          </details-card>
        </div>
        <div class="masonry-item w-100 d-inline-block mb-4">
          <details-card
            :title="t('domain.room.usableArea')"
            :icon="mdiTextureBox"
            :loading="getRoomLoading"
          >
            <v-list
              class="mt-0 py-0"
              density="compact"
            >
              <v-list-item>
                <v-list-item-title>
                  {{ roomData?.area }} {{ t("common.squareMeterAbr") }}
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </details-card>
        </div>
        <div class="masonry-item w-100 d-inline-block mb-4">
          <details-card
            :title="t('domain.room.capacity.header')"
            :subtitle="
              t('domain.room.capacity.msg', { num: roomData?.capacity })
            "
            :icon="mdiHumanCapacityIncrease"
            :loading="getRoomLoading"
          >
            <template #default>
              <v-list
                class="mt-0 py-0"
                density="compact"
              >
                <v-list-item
                  v-for="(capacity, idx) in roomData?.roomSeatingCapacities"
                  :key="idx"
                  v-tooltip:end="capacity.seatingType.description"
                  :title="capacity.seatingType.name"
                  :subtitle="`${capacity.capacity} Personen`"
                />
                <v-list-item
                  v-if="roomData?.roomSeatingCapacities.length === 0"
                  :subtitle="
                    t('generics.emptyList', {
                      domain: t('domain.seatingType.header'),
                    })
                  "
                />
              </v-list>
            </template>
          </details-card>
        </div>
        <div class="masonry-item w-100 d-inline-block mb-4">
          <details-card
            title="Mögliche Ausstattung"
            :icon="mdiSofaSingleOutline"
            :loading="getRoomLoading"
          >
            <v-list
              class="mt-0 py-0"
              density="compact"
            >
              <v-list-item
                v-for="(eq, idx) in roomData?.equipment"
                :key="idx"
                :title="eq.name"
                :subtitle="eq.description"
              />
              <v-list-item
                v-if="roomData?.equipment.length === 0"
                :subtitle="
                  t('generics.emptyList', {
                    domain: t('domain.equipment.header'),
                  })
                "
              />
            </v-list>
          </details-card>
        </div>
      </div>
    </template>
  </base-view>
</template>

<script setup lang="ts">
import type { RoomDetailsResponseDTO } from "@/api/raumreservierung-backend";

import {
  mdiAccountOutline,
  mdiArrowLeft,
  mdiCalendarQuestionOutline,
  mdiEmail,
  mdiHumanCapacityIncrease,
  mdiImage,
  mdiPencil,
  mdiPhone,
  mdiSofaSingleOutline,
  mdiTextureBox,
} from "@mdi/js";
import { computed, onMounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { useDisplay } from "vuetify/framework";

import BaseView from "@/components/common/BaseView.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import DetailsCard from "@/components/common/DetailsCard.vue";
import { useGetRoom } from "@/composables/api/useRoomsApi.ts";
import { useIsPrivileged } from "@/composables/useIsPrivileged.ts";
import { ROUTES } from "@/types/Routes.ts";

const router = useRouter();
const route = useRoute();

const { mdAndUp } = useDisplay();

const { t } = useI18n();

const id = computed(() => route.params.id as string | undefined);

const roomData = ref<RoomDetailsResponseDTO>();

const canEditRoom = useIsPrivileged("rooms:write");

const { data, isPending: getRoomLoading, refetch, error } = useGetRoom(id);

watch(error, async () => {
  if (error.value) {
    await router.replace({
      name: ROUTES.ROOMS_LIST,
    });
  }
});

// onMounted(async () => {
//   if (id.value) {
//     await refetch();
//
//     if (data.value) {
//       roomData.value = data.value as RoomDetailsResponseDTO;
//     } else {
//       await router.replace({
//         name: ROUTES.ROOMS_LIST,
//       });
//     }
//   }
// });
</script>

<style scoped>
.masonry-container {
  column-gap: 16px;
}

.masonry-cols-1 {
  column-count: 1;
}

.masonry-cols-2 {
  column-count: 2;
}

.masonry-item {
  break-inside: avoid-column;
}
</style>
