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
      <base-button
        :disabled="!isDeletable"
        class="ml-4"
        :text="t('generics.delete', { domain: t('domain.room.header') })"
        secondary
        :append-icon="mdiTrashCanOutline"
        @click="openDeleteDialog"
      />
      <v-dialog :model-value="showDialog">
        <confirm-card
          :title="
            dialogMode === 'delete'
              ? t('generics.delete', { domain: t('domain.room.header') })
              : t('views.roomEditView.deactivationConfirmTitle')
          "
          :loading="
            dialogMode === 'delete'
              ? deleteRoomLoading || deleteCheckLoading
              : updateRoomLoading
          "
          @cancel="closeDialog"
          @confirm="
            dialogMode === 'delete' ? handleDelete() : handleConfirmedSave()
          "
        >
          <template #text>
            <template v-if="dialogMode === 'delete'">
              <div v-if="canDeleteItem === undefined && !deleteCheckError">
                {{
                  t("generics.checkDelete", { domain: t("domain.room.header") })
                }}
              </div>
              <div v-else-if="canDeleteItem === true">
                {{
                  t("generics.confirmDelete", {
                    domain: t("domain.room.header"),
                  })
                }}
              </div>
              <div v-else>
                {{
                  t("generics.cantDelete", { domain: t("domain.room.header") })
                }}
              </div>
            </template>

            <template v-else-if="dialogMode === 'deactivate'">
              {{ t("views.roomEditView.deactivationFutureBookingsWarning") }}
            </template>
          </template>

          <template #cancel="{ props }">
            <base-button
              v-bind="props"
              secondary
              :text="
                dialogMode === 'delete' && !canDeleteItem
                  ? t('common.close')
                  : t('common.cancel')
              "
              :prepend-icon="mdiClose"
            />
          </template>

          <template #confirm="{ props }">
            <base-button
              v-if="dialogMode === 'deactivate' || canDeleteItem"
              :text="
                dialogMode === 'delete' ? t('common.delete') : t('common.save')
              "
              class="ml-4"
              :append-icon="
                dialogMode === 'delete'
                  ? mdiTrashCanOutline
                  : mdiContentSaveOutline
              "
              v-bind="props"
              :disabled="dialogMode === 'delete' && deleteCheckLoading"
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
          createRoomLoading ||
          deactivateCheckLoading
        "
        :loading="
          updateRoomLoading || createRoomLoading || deactivateCheckLoading
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
          <v-col
            cols="12"
            md="3"
          >
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
          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="roomData.weekScheduleCategory"
              :items="weekScheduleCategories"
              variant="outlined"
              :label="t('domain.weekScheduleCategory.title')"
              hide-details="auto"
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
              :room-id="roomId"
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
            <person-select
              v-model="contactPersonObject"
              :type="InternalPersonRequestDtoTypeEnum.INTERNAL"
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
  FindById200Response,
  RoomDetailsResponseDTO,
  RoomRequestDTO,
} from "@/api/raumreservierung-backend";
import { type DeepReadonly } from "vue";

import {
  mdiClose,
  mdiContentSaveOutline,
  mdiImageOutline,
  mdiTrashCanOutline,
  mdiWindowClose,
} from "@mdi/js";
import { computed, ref, toRaw, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

import {
  InternalPersonRequestDtoTypeEnum,
  RoomDetailsResponseDTOPropertyValidationAttributesMap,
} from "@/api/raumreservierung-backend";
import PersonSelect from "@/components/booking/PersonSelect.vue";
import BaseView from "@/components/common/BaseView.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CardForm from "@/components/common/CardForm.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";
import EquipmentSelector from "@/components/rooms/EquipmentSelector.vue";
import SeatingCapacityEditor from "@/components/rooms/SeatingCapacitySelector.vue";
import { useUploadFile } from "@/composables/api/useFileAttachmentApi.ts";
import {
  useCheckRoomHasNoFutureBookingsMutation,
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
import { STATUS_INDICATORS } from "@/constants.ts";
import { weekScheduleCategories } from "@/constants/WeekScheduleCategory.ts";

const isValid = ref<boolean>(false);
const roomData = ref<RoomRequestDTO>({ ...EMPTY_ROOM_DATA });

const router = useRouter();

const route = useRoute();

const snackbar = useSnackbarStore();

const roomId = computed(() => (route.params.id as string) || undefined);
const pictureMetaData = ref<File>();

const isDeletable = ref<boolean>(false);

const contactPersonObject = ref<FindById200Response | string>();

type DialogMode = "delete" | "deactivate" | null;
const dialogMode = ref<DialogMode>(null);
const showDialog = computed(() => dialogMode.value !== null);

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

const { mutate: checkDeactivation, isPending: deactivateCheckLoading } =
  useCheckRoomHasNoFutureBookingsMutation();

const {
  mutate: checkDeletable,
  data: canDeleteItem,
  isPending: deleteCheckLoading,
  isError: deleteCheckError,
  reset: resetDeleteCheck,
} = useCheckRoomHasNoFutureBookingsMutation();

watch(
  [() => roomReqData.value?.id, getRoomError],
  ([newId, hasError]) => {
    if (newId) {
      if (!hasError && roomReqData.value) {
        isDeletable.value = !roomReqData.value.isActive;
        roomData.value = mapResponseToRequest(
          toRaw(roomReqData.value) as RoomDetailsResponseDTO
        );
        contactPersonObject.value = roomData.value.contactPersonId ?? undefined;
        pictureMetaData.value = mapResponseToFileMock(
          roomReqData.value.picture
        );
      } else {
        router.push({ name: ROUTES.ROOMS_LIST });
      }
    } else {
      roomData.value = { ...EMPTY_ROOM_DATA };
      pictureMetaData.value = undefined;
      isDeletable.value = false;
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
  if (roomData.value.isActive || !roomId.value) {
    performSave();
    return;
  }

  checkDeactivation(roomId.value, {
    onSuccess: (hasNoFutureBookings) => {
      if (hasNoFutureBookings) {
        performSave();
      } else {
        dialogMode.value = "deactivate";
      }
    },
  });
};

const performSave = () => {
  const payload: RoomRequestDTO = {
    ...roomData.value,
    contactPersonId:
      typeof contactPersonObject.value === "string"
        ? contactPersonObject.value
        : contactPersonObject.value?.id,
  };

  if (roomId.value) {
    updateRoom(
      {
        roomId: roomId.value,
        roomRequestDTO: payload,
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
      { roomRequestDTO: payload },
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
  snackbar.push({
    color: STATUS_INDICATORS.SUCCESS,
    text: msg,
  });

  router.replace({
    name: ROUTES.ROOMS_DETAILS,
    params: { id: newRoomData.id },
  });
};

const handleConfirmedSave = () => {
  closeDialog();
  performSave();
};

const openDeleteDialog = () => {
  if (roomId.value) {
    resetDeleteCheck();
    dialogMode.value = "delete";
    checkDeletable(roomId.value);
  }
};

const closeDialog = () => {
  dialogMode.value = null;
  resetDeleteCheck();
};

const handleDelete = async () => {
  if (roomId.value) {
    deleteRoom(
      { roomId: roomId.value },
      {
        onSuccess: () => {
          snackbar.push({
            color: STATUS_INDICATORS.SUCCESS,
            text: t("generics.deleted", {
              domain: t("domain.room.header"),
            }),
          });
          router.push({ name: ROUTES.ROOMS_LIST });
        },
      }
    );
  }
};
</script>

<style scoped></style>
