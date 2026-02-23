<template>
  <div>
    <view-simple-header :header-text="t('views.equipment.header')" />
    <v-dialog
      :model-value="showDialog"
      width="90%"
      max-width="800px"
      persistent
      close-on-back
    >
      <equipment-form
        v-if="dialogMode === 'equipmentEdit'"
        v-model="selectedEquipment"
        @cancel="resetDialog"
        @save="updateEquipment"
        :loading="
          updateEquipmentLoading ||
          saveEquipmentLoading ||
          getAllEquipmentLoading
        "
      />
      <confirm-card
        v-else-if="dialogMode === 'confirmDel'"
        title="Ausstattung löschen"
        text="Möchten Sie die Ausstattung endgültig löschen?"
        @no="resetDialog"
        @yes="deleteEquipment"
      />
    </v-dialog>

    <card-table
      :items="allEquipmentsData || readonly([])"
      :headers="headers"
      :loading="getAllEquipmentLoading || deleteEquipmentLoading"
    >
      <template #action>
        <base-button @click="dialogMode = 'equipmentEdit'">
          <template #append>
            <v-icon :icon="mdiPlus" />
          </template>
          <template #default> Hinzufügen </template>
        </base-button>
      </template>
      <template v-slot:[tableActionSlotName]="{ item }">
        <v-row align-content="center">
          <v-col
            class="pa-0"
            cols="12"
            sm="6"
          >
            <action-button
              type="edit"
              class="mr-1"
              @click="promptEditEquipment(item)"
            />
          </v-col>
          <v-col
            class="pa-0"
            cols="12"
            sm="6"
          >
            <action-button
              type="delete"
              @click="promptDeleteEquipment(item)"
            />
          </v-col>
        </v-row>
      </template>
    </card-table>
  </div>
</template>

<script setup lang="ts">
import type { EquipmentResponseDto } from "@/api/raumreservierung-backend";
import type { TableHeader } from "@/components/common/CardTable.vue";

import { mdiPlus } from "@mdi/js";
import { computed, onMounted, readonly, ref, unref } from "vue";
import { useI18n } from "vue-i18n";

import { Levels } from "@/api/error.ts";
import {
  EquipmentResponseDtoFromJSON,
  EquipmentResponseDtoToJSONTyped,
} from "@/api/raumreservierung-backend";
import ActionButton from "@/components/common/buttons/ActionButton.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CardTable from "@/components/common/CardTable.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";
import ViewSimpleHeader from "@/components/common/ViewSimpleHeader.vue";
import EquipmentForm from "@/components/EquipmentForm.vue";
import {
  useCreateEquipment,
  useDeleteEquipment,
  useGetAllEquipments,
  useUpdateEquipment,
} from "@/composables/api/useEquipmentApi.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

type DialogMode = "equipmentEdit" | "confirmDel" | null;

const { t } = useI18n();

const snackbarStore = useSnackbarStore();

const tableActionSlotName = ref("item.actions");
const dialogMode = ref<DialogMode>(null);
const showDialog = computed(() => dialogMode.value !== null);

const selectedEquipment = ref<EquipmentResponseDto>({
  name: "",
  description: "",
});

const {
  data: allEquipmentsData,
  call: getAllEquipments,
  loading: getAllEquipmentLoading,
} = useGetAllEquipments();

const { call: deleteEquipmentCall, loading: deleteEquipmentLoading } =
  useDeleteEquipment();

const { call: saveEquipmentCall, loading: saveEquipmentLoading } =
  useCreateEquipment();

const { call: updateEquipmentCall, loading: updateEquipmentLoading } =
  useUpdateEquipment();

onMounted(() => getAllEquipments());

const headers: TableHeader<EquipmentResponseDto>[] = [
  { title: "Name", value: "name", sortable: true },
  { title: "Description", value: "description" },
  { title: "Aktionen", value: "actions" },
];

const promptDeleteEquipment = (value: EquipmentResponseDto) => {
  selectedEquipment.value = value;
  dialogMode.value = "confirmDel";
};

const promptEditEquipment = (value: EquipmentResponseDto) => {
  selectedEquipment.value = EquipmentResponseDtoFromJSON(
    EquipmentResponseDtoToJSONTyped(value)
  );

  dialogMode.value = "equipmentEdit";
};

// Executions
const deleteEquipment = async () => {
  if (selectedEquipment.value.id) {
    await deleteEquipmentCall({ body: selectedEquipment.value.id });
  }
  await getAllEquipments();
  snackbarStore.add({
    message: "Ausstattung gelöscht",
    level: Levels.SUCCESS,
  });
  resetDialog();
};

const updateEquipment = async () => {
  if (selectedEquipment.value.id) {
    await updateEquipmentCall({
      equipmentRequestDto: unref(selectedEquipment),
      equipmentId: selectedEquipment.value.id,
    });
  } else {
    await saveEquipmentCall({
      equipmentRequestDto: unref(selectedEquipment),
    });
  }

  await getAllEquipments();
  snackbarStore.add({
    message: "Ausstattung gespeichert",
    level: Levels.SUCCESS,
  });
  resetDialog();
};

const resetDialog = () => {
  selectedEquipment.value = { name: "", description: "" };
  dialogMode.value = null;
};
</script>

<style scoped></style>
