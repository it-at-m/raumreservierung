<template>
  <base-view
    :header-text="
      t('generics.manage', { domain: t('domain.equipment.header') })
    "
  >
    <crud-card
      ref="crudRef"
      :empty-item-template="EMPTY_ITEM_TEMPLATE"
      :loading="getAllEquipmentLoading || deleteEquipmentLoading"
      :domain="t('domain.equipment.header')"
      @delete="handleDelete"
      @create="handleCreate"
      @update="handleUpdate"
    >
      <template #form="{ item, updateItem, updateValidity }">
        <equipment-form
          :model-value="item"
          :disabled="updateEquipmentLoading || saveEquipmentLoading"
          @update:model-value="updateItem"
          @is-valid="updateValidity"
        />
      </template>
      <template #table="{ openEdit, openDelete }">
        <v-data-table
          :headers="headers"
          :items="allEquipmentsData || []"
          hide-default-footer
          items-per-page="-1"
        >
          <template #[`item.isActive`]="{ item }">
            <v-icon :icon="item.isActive ? mdiCheck : mdiMinus" />
          </template>
          <template #[`item.actions`]="{ item }">
            <rr-button-group>
              <action-button
                type="edit"
                class="mr-1"
                @click="openEdit(item)"
              />
              <action-button
                :disabled="item.isActive"
                type="delete"
                @click="openDelete(item)"
              />
            </rr-button-group>
          </template>
        </v-data-table>
      </template>
    </crud-card>
  </base-view>
</template>

<script setup lang="ts">
import type { EquipmentResponseDto } from "@/api/raumreservierung-backend";
import type { TableHeader } from "@/types/TableHeader.ts";

import { mdiCheck, mdiMinus } from "@mdi/js";
import { useTemplateRef } from "vue";
import { useI18n } from "vue-i18n";

import { Levels } from "@/api/error.ts";
import BaseView from "@/components/common/BaseView.vue";
import ActionButton from "@/components/common/buttons/ActionButton.vue";
import RrButtonGroup from "@/components/common/buttons/rrButtonGroup.vue";
import CrudCard from "@/components/common/CrudCard.vue";
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

const { data: allEquipmentsData, isPending: getAllEquipmentLoading } =
  useGetAllEquipments();

const {
  mutateAsync: deleteEquipmentCall,
  isPending: deleteEquipmentLoading,
  error: deleteEquipmentError,
} = useDeleteEquipment();

const {
  mutateAsync: saveEquipmentCall,
  isPending: saveEquipmentLoading,
  error: saveEquipmentError,
} = useCreateEquipment();

const {
  mutateAsync: updateEquipmentCall,
  isPending: updateEquipmentLoading,
  error: updateEquipmentError,
} = useUpdateEquipment();

const headers: TableHeader<EquipmentResponseDto>[] = [
  { title: t("domain.equipment.name"), value: "name", sortable: true },
  { title: t("domain.equipment.description"), value: "description" },
  {
    title: t("domain.equipment.isActive"),
    value: "isActive",
    sortable: true,
    align: "center",
  },
  {
    title: t("common.action", { count: 2 }),
    value: "actions",
    width: "1%",
    align: "center",
  },
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
  if (crudRef.value) {
    crudRef.value.closeDialog();
  }
  snackbarStore.add({ message: msg, level: Levels.SUCCESS });
};
</script>

<style scoped></style>
