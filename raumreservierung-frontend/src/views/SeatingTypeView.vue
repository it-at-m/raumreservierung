<template>
  <generic-table-crud-view
    ref="crudRef"
    :domain="t('domain.seatingType.header')"
    :items="allSeatingTypesData || []"
    :headers="headers"
    :loading="getAllSeatingTypeLoading || deleteSeatingTypeLoading"
    :empty-item-template="EMPTY_ITEM_TEMPLATE"
    @create="handleCreate"
    @update="handleUpdate"
    @delete="handleDelete"
  >
    <template #form="{ item, save, cancel, updateItem }">
      <confirm-card
        :loading="updateSeatingTypeLoading || createSeatingTypeLoading"
        :title="
          item.id
            ? t('generics.edit', { domain: t('domain.equipment.header') })
            : t('generics.create', { domain: t('domain.equipment.header') })
        "
        @confirm="save"
        @cancel="cancel"
      >
        <template #text>
          <seating-type-form
            :model-value="item"
            @update:model-value="updateItem"
            :disabled="updateSeatingTypeLoading || createSeatingTypeLoading"
          />
        </template>
        <template #confirm="{ props }">
          <base-button
            :text="t('common.save')"
            :append-icon="mdiContentSaveOutline"
            v-bind="props"
          />
        </template>
      </confirm-card>
    </template>
  </generic-table-crud-view>
</template>

<script setup lang="ts">
import type { SeatingTypeResponseDto } from "@/api/raumreservierung-backend";
import type { TableHeader } from "@/components/common/CardTable.vue";

import { mdiContentSaveOutline } from "@mdi/js";
import { onMounted, useTemplateRef } from "vue";
import { useI18n } from "vue-i18n";

import { Levels } from "@/api/error.ts";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";
import GenericTableCrudView from "@/components/common/GenericTableCrudView.vue";
import SeatingTypeForm from "@/components/common/SeatingTypeForm.vue";
import EquipmentForm from "@/components/EquipmentForm.vue";
import {
  useCreateSeatingType,
  useDeleteSeatingType,
  useGetAllSeatingTypes,
  useUpdateSeatingType,
} from "@/composables/api/useSeatingApi.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const { t } = useI18n();

const snackbarStore = useSnackbarStore();

const crudRef = useTemplateRef("crudRef");

const {
  data: allSeatingTypesData,
  call: getAllSeatingTypes,
  loading: getAllSeatingTypeLoading,
} = useGetAllSeatingTypes();

const {
  call: deleteSeatingTypeCall,
  loading: deleteSeatingTypeLoading,
  error: deleteSeatingTypeError,
} = useDeleteSeatingType();

const {
  call: createSeatingTypeCall,
  loading: createSeatingTypeLoading,
  error: createSeatingTypeError,
} = useCreateSeatingType();

const {
  call: updateSeatingTypeCall,
  loading: updateSeatingTypeLoading,
  error: updateSeatingTypeError,
} = useUpdateSeatingType();

onMounted(() => getAllSeatingTypes());

const headers: TableHeader<SeatingTypeResponseDto>[] = [
  { title: t("domain.seatingType.name"), value: "name", sortable: true },
  { title: t("domain.seatingType.description"), value: "description" },
  { title: t("common.action", { count: 2 }), value: "actions" },
];

const EMPTY_ITEM_TEMPLATE = {
  name: "",
  description: "",
} as SeatingTypeResponseDto;

const handleCreate = async (newItem: SeatingTypeResponseDto) => {
  await createSeatingTypeCall({ seatingTypeRequestDto: newItem });
  if (!createSeatingTypeError.value) {
    await onSuccess("Ausstattung erstellt");
  }
};

const handleUpdate = async (updatedItem: SeatingTypeResponseDto) => {
  if (updatedItem.id) {
    await updateSeatingTypeCall({
      seatingTypeRequestDto: updatedItem,
      seatingTypeId: updatedItem.id,
    });
    if (!updateSeatingTypeError.value) {
      await onSuccess("Ausstattung aktualisiert");
    }
  }
};

const handleDelete = async (id: string) => {
  await deleteSeatingTypeCall({ body: id });
  if (!deleteSeatingTypeError.value) {
    await onSuccess("Ausstattung gelöscht");
  }
};

const onSuccess = async (msg: string) => {
  await getAllSeatingTypes();
  if (crudRef.value) {
    crudRef.value.closeDialog();
  }
  snackbarStore.add({ message: msg, level: Levels.SUCCESS });
};
</script>

<style scoped></style>
