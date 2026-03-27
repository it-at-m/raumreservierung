<template>
  <generic-table-crud-view
    ref="crudRef"
    :domain="t('domain.equipment.header')"
    :items="allEquipmentsData || []"
    :headers="headers"
    :loading="getAllEquipmentLoading || deleteEquipmentLoading"
    :empty-item-template="EMPTY_ITEM_TEMPLATE"
    @create="handleCreate"
    @update="handleUpdate"
    @delete="handleDelete"
  >
    <template #form="{ item, updateItem, updateValidity }">
      <equipment-form
        :model-value="item"
        @update:model-value="updateItem"
        @is-valid="updateValidity"
        :disabled="updateEquipmentLoading || saveEquipmentLoading"
      />
    </template>
    <template #[`item.isActive`]="{ item }">
      <v-checkbox-btn
        readonly
        hide-details
        :model-value="item.isActive"
        class="pointer-events-none"
      />
    </template>
    <template #itemActions="{ openEdit, promptDelete, item }">
      <v-row align-content="center">
        <v-col
          class="pa-0"
          cols="12"
          sm="6"
        >
          <action-button
            type="edit"
            class="mr-1"
            @click="openEdit(item)"
          />
        </v-col>
        <v-col
          class="pa-0"
          cols="12"
          sm="6"
        >
          <action-button
            :disabled="item.isActive"
            type="delete"
            @click="promptDelete(item)"
          />
        </v-col>
      </v-row>
    </template>
  </generic-table-crud-view>
</template>

<script setup lang="ts">
import type { EquipmentResponseDto } from "@/api/raumreservierung-backend";
import type { TableHeader } from "@/types/TableHeader.ts";

import { onMounted, useTemplateRef } from "vue";
import { useI18n } from "vue-i18n";

import { Levels } from "@/api/error.ts";
import ActionButton from "@/components/common/buttons/ActionButton.vue";
import GenericTableCrudView from "@/components/common/GenericTableCrudView.vue";
import EquipmentForm from "@/components/EquipmentForm.vue";
import {
  useCreateEquipment,
  useDeleteEquipment,
  useGetAllEquipments,
  useUpdateEquipment,
} from "@/composables/api/useEquipmentApi.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const { t } = useI18n();

const EMPTY_ITEM_TEMPLATE = {
  name: "",
  description: "",
} as EquipmentResponseDto;

const snackbarStore = useSnackbarStore();

const crudRef = useTemplateRef("crudRef");

const {
  data: allEquipmentsData,
  call: getAllEquipments,
  loading: getAllEquipmentLoading,
} = useGetAllEquipments();

const {
  call: deleteEquipmentCall,
  loading: deleteEquipmentLoading,
  error: deleteEquipmentError,
} = useDeleteEquipment();

const {
  call: saveEquipmentCall,
  loading: saveEquipmentLoading,
  error: saveEquipmentError,
} = useCreateEquipment();

const {
  call: updateEquipmentCall,
  loading: updateEquipmentLoading,
  error: updateEquipmentError,
} = useUpdateEquipment();

onMounted(() => getAllEquipments());

const headers: TableHeader<EquipmentResponseDto>[] = [
  { title: t("domain.equipment.name"), value: "name", sortable: true },
  { title: t("domain.equipment.description"), value: "description" },
  { title: t("domain.equipment.isActive"), value: "isActive", sortable: true },
  { title: t("common.action", { count: 2 }), value: "actions" },
];

const handleCreate = async (newItem: EquipmentResponseDto) => {
  await saveEquipmentCall({ equipmentRequestDto: newItem });
  if (!saveEquipmentError.value) {
    await onSuccess(
      t("generics.created", { domain: t("domain.equipment.header") })
    );
  }
};

const handleUpdate = async (updatedItem: EquipmentResponseDto) => {
  if (updatedItem.id) {
    await updateEquipmentCall({
      equipmentRequestDto: updatedItem,
      equipmentId: updatedItem.id,
    });
    if (!updateEquipmentError.value) {
      await onSuccess(
        t("generics.updated", { domain: t("domain.equipment.header") })
      );
    }
  }
};

const handleDelete = async (id: string) => {
  await deleteEquipmentCall({ equipmentId: id });
  if (!deleteEquipmentError.value) {
    await onSuccess(
      t("generics.deleted", { domain: t("domain.equipment.header") })
    );
  }
};

const onSuccess = async (msg: string) => {
  await getAllEquipments();
  if (crudRef.value) {
    crudRef.value.closeDialog();
  }
  snackbarStore.add({ message: msg, level: Levels.SUCCESS });
};
</script>

<style scoped></style>
