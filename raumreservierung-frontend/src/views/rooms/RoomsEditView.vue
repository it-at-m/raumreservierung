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
            @click="deleteRoomId = roomId"
          />
        </template>
        <template #default="{ isActive }">
          <confirm-card
            :title="t('generics.delete', { domain: t('domain.room.header') })"
            :text="
              t('generics.confirmMsg', { domain: t('domain.room.header') })
            "
            :loading="deleteRoomLoading || deleteCheckLoading"
            @cancel="isActive.value = false"
            @confirm="
              handleDelete();
              isActive.value = false;
            "
          >
            <template #text>
              <div v-if="deleteCheckLoading">
                {{ t("generics.checkDelete") }}
              </div>
              <div v-if="!deleteCheckLoading && canDeleteItem === true">
                {{
                  t("generics.confirmDelete", {
                    domain: t("domain.room.header"),
                  })
                }}
              </div>
              <v-alert
                v-if="canDeleteItem === false"
                type="warning"
                variant="outlined"
                density="compact"
              >
                {{ t("generics.cantDelete") }}
              </v-alert>
            </template>
            <template #confirm="{ props }">
              <base-button
                :text="t('common.delete')"
                :append-icon="mdiTrashCanOutline"
                v-bind="props"
                :disabled="deleteCheckLoading || !canDeleteItem"
              />
            </template>
          </confirm-card>
        </template>
      </v-dialog>
      <v-dialog
        v-model="showDeactivateConfirm"
        max-width="800px"
        width="90%"
      >
        <confirm-card
          :title="t('views.roomEditView.deactivationConfirmTitle')"
          @cancel="showDeactivateConfirm = false"
          @confirm="handleConfirmedSave"
        >
          <template #text>
            <v-alert
              v-if="canDeactivate === false"
              type="warning"
              variant="outlined"
              density="compact"
            >
              {{ t("views.roomEditView.deactivationFutureBookingsWarning") }}
            </v-alert>
          </template>
          <template #confirm="{ props }">
            <base-button
              :text="t('common.save')"
              v-bind="props"
            />
          </template>
        </confirm-card>
      </v-dialog>
      <base-button
        class="ml-4"
        :text="t('common.save')"
        :append-icon="mdiContentSaveOutline"
        :disabled="
          !isValid ||
          uploadFileLoading ||
          updateRoomLoading ||
          createRoomLoading
        "
        @click="handleSave"
      />
    </template>

    <template #default>
      <v-form
        v-model="isValid"
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
              :loading="uploadFileLoading"
              :disabled="uploadFileLoading"
              :model-value="pictureMetaData"
              clearable
              color="accent"
              show-size
              prepend-icon=""
              accept="image/png, image/jpeg"
              :prepend-inner-icon="mdiImageOutline"
              :label="t('domain.room.image')"
              variant="outlined"
              @update:model-value="uploadPicture"
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
import { computed, ref, toRaw, watch } from "vue";
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
import { useUploadFile } from "@/composables/api/useFileAttachmentApi.ts";
import {
  useCheckRoomDeletable,
  useCreateRoom,
  useDeleteRoom,
  useGetRoom,
  useUpdateRoom,
} from "@/composables/api/useRoomsApi.ts";
import { useRules } from "@/composables/useRules.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { ROUTES } from "@/types/Routes.ts";
import { mapResponseToFileMock } from "@/util/fileTypeUtility.ts";
import { EMPTY_ROOM_DATA, mapResponseToRequest } from "@/util/roomTypeUtil.ts";

const isValid = ref<boolean>(false);
const roomData = ref<RoomRequestDTO>(EMPTY_ROOM_DATA);

const showDeactivateConfirm = ref<boolean>(false);
const deactivateCheckId = ref<string>();

const router = useRouter();

const route = useRoute();

const snackbar = useSnackbarStore();

const roomId = computed(() => (route.params.id as string) || undefined);
const pictureMetaData = ref<File>();

const isDeletable = ref<boolean>(false);
const deleteRoomId = ref<string>();

const { t } = useI18n();

const rules = useRules();

const {
  data: roomReqData,
  isLoading: getRoomLoading,
  error: getRoomError,
} = useGetRoom(roomId);

const { mutate: updateRoom, isPending: updateRoomLoading } = useUpdateRoom();

const { mutate: createRoom, isPending: createRoomLoading } = useCreateRoom();

const { mutate: deleteRoom, isPending: deleteRoomLoading } = useDeleteRoom();

const {
  mutateAsync: uploadFile,
  isPending: uploadFileLoading,
  data: uploadFileData,
  error: uploadFileError,
} = useUploadFile();

const { data: canDeleteItem, isFetching: deleteCheckLoading } =
  useCheckRoomDeletable(deleteRoomId);

const { data: canDeactivate, refetch: refetchDeactivateCheck } =
  useCheckRoomDeletable(deactivateCheckId);

watch(
  [() => roomReqData.value?.id, getRoomError],
  ([newId, hasError]) => {
    if (newId) {
      if (!hasError && roomReqData.value) {
        isDeletable.value = !roomReqData.value.isActive;
        roomData.value = mapResponseToRequest(
          toRaw(roomReqData.value) as RoomDetailsResponseDTO
        );

        pictureMetaData.value = mapResponseToFileMock(
          roomReqData.value.picture
        );
      } else {
        router.push({ name: ROUTES.ROOMS_LIST });
        return;
      }
    }
  },
  { immediate: true }
);

const uploadPicture = async (value: File | File[]) => {
  const pictureToUpload = Array.isArray(value) ? value[0] : value;

  if (pictureToUpload) {
    await uploadFile({ file: pictureToUpload });
    if (uploadFileError.value) {
      return;
    }
  }

  const fileData = pictureToUpload ? uploadFileData.value : undefined;

  pictureMetaData.value = mapResponseToFileMock(fileData);
  roomData.value = {
    ...roomData.value,
    pictureId: fileData?.id,
  };
};

const handleSave = () => {
  if (roomData.value.isActive === false && roomId.value) {
    deactivateCheckId.value = roomId.value;
    refetchDeactivateCheck().then(({ data }) => {
      if (data === false) {
        showDeactivateConfirm.value = true;
      } else {
        performSave();
      }
    });
    return;
  }
  performSave();
};

const handleConfirmedSave = () => {
  showDeactivateConfirm.value = false;
  performSave();
};

const performSave = () => {
  if (roomId.value) {
    updateRoom(
      {
        roomId: roomId.value,
        roomRequestDTO: roomData.value,
      },
      {
        onSuccess: (updatedRoom) => {
          onSuccess(
            updatedRoom,
            t("generics.updated", { domain: t("domain.room.header") })
          );
        },
      }
    );
  } else {
    createRoom(
      { roomRequestDTO: roomData.value },
      {
        onSuccess: (createdRoom) => {
          onSuccess(
            createdRoom,
            t("generics.created", { domain: t("domain.room.header") })
          );
        },
      }
    );
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
    deleteRoom(
      { roomId: roomId.value },
      {
        onSuccess: () => {
          snackbar.add({
            level: Levels.SUCCESS,
            message: t("generics.deleted", { domain: t("domain.room.header") }),
          });
          router.push({ name: ROUTES.ROOMS_LIST });
        },
      }
    );
  }
};
</script>

<style scoped></style>
