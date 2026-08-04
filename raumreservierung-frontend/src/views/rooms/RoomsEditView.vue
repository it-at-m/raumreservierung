<template>
  <base-view
    :header-text="
      roomId
        ? t('generics.edit', { domain: t('domain.room.header') })
        : t('generics.create', { domain: t('domain.room.header') })
    "
  >
    <template #headerActions>
      <base-button
        class="ml-4"
        :text="t('common.cancel')"
        secondary
        :prepend-icon="mdiWindowClose"
        @click="router.back()"
      />
      <v-dialog
        max-width="800px"
        width="90%"
      >
        <template #activator="{ props }">
          <base-button
            :disabled="!isDeletable"
            v-bind="props"
            class="ml-4"
            :text="t('generics.delete', { domain: t('domain.room.header') })"
            secondary
            :append-icon="mdiTrashCanOutline"
          />
        </template>
        <template #default="{ isActive }">
          <confirm-card
            :title="t('generics.delete', { domain: t('domain.room.header') })"
            :text="
              t('generics.confirmMsg', { domain: t('domain.room.header') })
            "
            :loading="deleteRoomLoading"
            @cancel="isActive.value = false"
            @confirm="
              handleDelete();
              isActive.value = false;
            "
          >
            <template #confirm="{ props }">
              <base-button
                :text="t('common.delete')"
                :append-icon="mdiTrashCanOutline"
                v-bind="props"
              />
            </template>
          </confirm-card>
        </template>
      </v-dialog>
      <base-button
        class="ml-4"
        :text="t('common.save')"
        :append-icon="mdiContentSaveOutline"
        @click="handleSave"
      />
    </template>

    <template #default>
      <v-form
        :readonly="getRoomLoading || updateRoomLoading || createRoomLoading"
      >
        <v-row>
          <v-col>
            <v-text-field
              v-model="roomData.name"
              color="accent"
              :label="t('domain.room.name')"
              :rules="[
                rules.required(
                  t('common.rules.notEmpty', { field: t('domain.room.name') })
                ),
                rules.maxLength(
                  RoomDetailsResponseDTOPropertyValidationAttributesMap.name
                    ?.maxLength ?? 100,
                  t('common.rules.maxLengthError', {
                    field: t('domain.room.name'),
                    num:
                      RoomDetailsResponseDTOPropertyValidationAttributesMap.name
                        ?.maxLength ?? 100,
                  })
                ),
              ]"
            />
          </v-col>
          <v-col>
            <v-text-field
              v-model="roomData.location"
              :rules="[
                rules.required(
                  t('common.rules.notEmpty', {
                    field: t('domain.room.location'),
                  })
                ),
                rules.maxLength(
                  RoomDetailsResponseDTOPropertyValidationAttributesMap.location
                    ?.maxLength ?? 255,
                  t('common.rules.maxLengthError', {
                    field: t('domain.room.number'),
                    num:
                      RoomDetailsResponseDTOPropertyValidationAttributesMap
                        .location?.maxLength ?? 255,
                  })
                ),
              ]"
              color="accent"
              :label="t('domain.room.location')"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-text-field
              v-model="roomData.number"
              color="accent"
              :label="t('domain.room.number')"
              :rules="[
                rules.required(
                  t('common.rules.notEmpty', { field: t('domain.room.number') })
                ),
                rules.maxLength(
                  RoomDetailsResponseDTOPropertyValidationAttributesMap.number
                    ?.maxLength ?? 100,
                  t('common.rules.maxLengthError', {
                    field: t('domain.room.number'),
                    num:
                      RoomDetailsResponseDTOPropertyValidationAttributesMap
                        .number?.maxLength ?? 100,
                  })
                ),
              ]"
            />
          </v-col>
          <v-col>
            <v-file-input
              color="accent"
              prepend-icon=""
              :prepend-inner-icon="mdiImageOutline"
              readonly
              :label="t('domain.room.image')"
              variant="outlined"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-textarea
              v-model="roomData.locationDescription"
              :rules="[
                rules.maxLength(
                  RoomDetailsResponseDTOPropertyValidationAttributesMap
                    .locationDescription?.maxLength ?? 500,
                  t('common.rules.maxLengthError', {
                    field: t('domain.room.number'),
                    num:
                      RoomDetailsResponseDTOPropertyValidationAttributesMap
                        .locationDescription?.maxLength ?? 500,
                  })
                ),
              ]"
              color="accent"
              variant="outlined"
              :label="t('domain.room.locationDescription')"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <equipment-selector v-model="roomData.equipmentIds" />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <seating-capacity-editor
              v-model="roomData.roomSeatingCapacities!"
              :max-room-capacity="roomData.capacity"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-number-input
              v-model="roomData.area"
              :min="1"
              :suffix="t('common.squareMeterAbr')"
              inset
              color="accent"
              :label="t('domain.room.usableArea')"
              variant="outlined"
            />
          </v-col>
          <v-col>
            <v-number-input
              v-model="roomData.capacity"
              :min="1"
              :suffix="t('domain.person.header', { count: roomData.capacity })"
              inset
              color="accent"
              variant="outlined"
              :label="t('domain.room.capacity.max')"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-text-field
              v-model="roomData.contactPersonId"
              color="accent"
              readonly
              :label="t('domain.room.contactPerson')"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <card-form :subtitle="t('domain.room.isActiveHeader')">
              <template #text>
                <v-radio-group
                  v-model="roomData.isActive"
                  inline
                  color="accent"
                  hide-details
                >
                  <v-radio
                    :value="true"
                    :label="
                      t('generics.active', { domain: t('domain.room.header') })
                    "
                    class="mr-4"
                  />
                  <v-radio
                    :label="
                      t('generics.inActive', {
                        domain: t('domain.room.header'),
                      })
                    "
                    :value="false"
                  />
                </v-radio-group>
              </template>
            </card-form>
          </v-col>
        </v-row>
      </v-form>
    </template>
  </base-view>
</template>

<script setup lang="ts">
import type {
  RoomDetailsResponseDTO,
  RoomRequestDTO,
} from "@/api/raumreservierung-backend";
import type { DeepReadonly } from "vue";

import {
  mdiContentSaveOutline,
  mdiImageOutline,
  mdiTrashCanOutline,
  mdiWindowClose,
} from "@mdi/js";
import { computed, onMounted, ref, toRaw, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

import { Levels } from "@/api/error.ts";
import { RoomDetailsResponseDTOPropertyValidationAttributesMap } from "@/api/raumreservierung-backend";
import BaseView from "@/components/common/BaseView.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CardForm from "@/components/common/CardForm.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";
import EquipmentSelector from "@/components/rooms/EquipmentSelector.vue";
import SeatingCapacityEditor from "@/components/rooms/SeatingCapacitySelector.vue";
import {
  useCreateRoom,
  useDeleteRoom,
  useGetRoom,
  useUpdateRoom,
} from "@/composables/api/useRoomsApi.ts";
import { useRules } from "@/composables/useRules.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { ROUTES } from "@/types/Routes.ts";
import { EMPTY_ROOM_DATA, mapResponseToRequest } from "@/util/roomTypeUtil.ts";

const roomData = ref<RoomRequestDTO>(EMPTY_ROOM_DATA);

const router = useRouter();

const route = useRoute();

const snackbar = useSnackbarStore();

const roomId = computed(() => (route.params.id as string) || undefined);

const isDeletable = ref<boolean>(false);

const { t } = useI18n();

const {
  data: roomReqData,
  isPending,
  error: getRoomError,
} = useGetRoom(roomId);

const getRoomLoading = computed(() => isPending.value && !!roomId.value);

const rules = useRules();

const {
  mutateAsync: updateRoom,
  data: updateRoomData,
  isPending: updateRoomLoading,
  error: updateRoomError,
} = useUpdateRoom();

const {
  mutateAsync: createRoom,
  data: createRoomData,
  isPending: createRoomLoading,
  error: createRoomError,
} = useCreateRoom();

const {
  mutateAsync: deleteRoom,
  isPending: deleteRoomLoading,
  error: deleteRoomError,
} = useDeleteRoom();

watch(
  [() => roomReqData.value?.id, getRoomError],
  ([newId, hasError]) => {
    if (newId) {
      if (!hasError && roomReqData.value) {
        isDeletable.value = !roomReqData.value.isActive;
        roomData.value = mapResponseToRequest(
          toRaw(roomReqData.value) as RoomDetailsResponseDTO
        );
      } else {
        router.push({ name: ROUTES.ROOMS_LIST });
        return;
      }
    }
  },
  { immediate: true }
);

onMounted(() => {
  if (!roomId.value) {
    roomData.value = { ...EMPTY_ROOM_DATA };
  }
});

const handleSave = async () => {
  if (roomId.value) {
    await updateRoom({
      roomId: roomId.value,
      roomRequestDTO: roomData.value,
    });

    if (!updateRoomError.value && updateRoomData.value) {
      onSuccess(
        updateRoomData.value,
        t("generics.updated", { domain: t("domain.room.header") })
      );
    }
  } else {
    await createRoom({ roomRequestDTO: roomData.value });

    if (!createRoomError.value && createRoomData.value) {
      onSuccess(
        createRoomData.value,
        t("generics.created", { domain: t("domain.room.header") })
      );
    }
  }
};

const onSuccess = (
  newRoomData: DeepReadonly<RoomDetailsResponseDTO>,
  msg: string
) => {
  snackbar.add({
    level: Levels.SUCCESS,
    message: msg,
  });

  router.replace({
    name: ROUTES.ROOMS_DETAILS,
    params: { id: newRoomData.id },
  });
};

const handleDelete = async () => {
  if (roomId.value) {
    await deleteRoom({ roomId: roomId.value });

    if (!deleteRoomError.value) {
      snackbar.add({
        level: Levels.SUCCESS,
        message: t("generics.deleted", { domain: t("domain.room.header") }),
      });

      await router.push({ name: ROUTES.ROOMS_LIST });
    }
  }
};
</script>

<style scoped></style>
