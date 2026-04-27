<template>
  <base-view
    :header-text="
      t('generics.manage', { domain: t('domain.seatingType.header') })
    "
  >
    <template #default>
      <crud-card
        ref="crudRef"
        :empty-item-template="EMPTY_ITEM_TEMPLATE"
        :loading="getAllSeatingTypeLoading || deleteSeatingTypeLoading"
        :domain="t('domain.seatingType.header')"
        @create="handleCreate"
        @update="handleUpdate"
        @delete="handleDelete"
      >
        <template #form="{ item, updateItem, updateValidity }">
          <seating-type-form
            :model-value="item"
            @update:model-value="updateItem"
            @is-valid="updateValidity"
            :disabled="updateSeatingTypeLoading || createSeatingTypeLoading"
          />
        </template>
        <template #table="{ openEdit, openDelete }">
          <v-data-table
            :headers="headers"
            :items="allSeatingTypesData || []"
            hide-default-footer
            items-per-page="-1"
          >
            <template #[`item.isActive`]="{ item }">
              <v-checkbox-btn
                readonly
                hide-details
                :model-value="item.isActive"
                class="pointer-events-none"
              />
            </template>
            <template #[`item.actions`]="{ item }">
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
                    @click="openDelete(item)"
                  />
                </v-col>
              </v-row>
            </template>
          </v-data-table>
        </template>
      </crud-card>
    </template>
  </base-view>
</template>

<script setup lang="ts">
import type { SeatingTypeResponseDto } from "@/api/raumreservierung-backend";
import type { TableHeader } from "@/types/TableHeader.ts";

import { onMounted, useTemplateRef } from "vue";
import { useI18n } from "vue-i18n";

import { Levels } from "@/api/error.ts";
import BaseView from "@/components/common/BaseView.vue";
import ActionButton from "@/components/common/buttons/ActionButton.vue";
import CrudCard from "@/components/common/CrudCard.vue";
import SeatingTypeForm from "@/components/SeatingTypeForm.vue";
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
  {
    title: t("domain.seatingType.isActive"),
    value: "isActive",
    sortable: true,
  },
  { title: t("common.action", { count: 2 }), value: "actions" },
];

const EMPTY_ITEM_TEMPLATE = {
  name: "",
  description: "",
} as SeatingTypeResponseDto;

const handleCreate = async (newItem: SeatingTypeResponseDto) => {
  await createSeatingTypeCall({ seatingTypeRequestDto: newItem });
  if (!createSeatingTypeError.value) {
    await onSuccess(
      t("generics.created", { domain: t("domain.seatingType.header") })
    );
  }
};

const handleUpdate = async (updatedItem: SeatingTypeResponseDto) => {
  if (updatedItem.id) {
    await updateSeatingTypeCall({
      seatingTypeRequestDto: updatedItem,
      seatingTypeId: updatedItem.id,
    });
    if (!updateSeatingTypeError.value) {
      await onSuccess(
        t("generics.updated", { domain: t("domain.seatingType.header") })
      );
    }
  }
};

const handleDelete = async (id: string) => {
  await deleteSeatingTypeCall({ seatingTypeId: id });
  if (!deleteSeatingTypeError.value) {
    await onSuccess(
      t("generics.deleted", { domain: t("domain.seatingType.header") })
    );
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
