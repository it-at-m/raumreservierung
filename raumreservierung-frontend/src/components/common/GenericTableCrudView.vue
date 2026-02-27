<template>
  <div>
    <view-simple-header :header-text="t('generics.manage', { domain })" />

    <v-dialog
      :model-value="showDialog"
      width="90%"
      :max-width="maxDialogWidth"
      persistent
      close-on-back
    >
      <template v-if="dialogMode === 'form'">
        <slot
          name="form"
          :item="activeItem"
          :updateItem="updateActiveItem"
          :save="handleSave"
          :cancel="closeDialog"
        />
      </template>

      <confirm-card
        v-else-if="dialogMode === 'delete'"
        :title="t('generics.delete', { domain })"
        :text="t('generics.confirmDelete', { domain })"
        :loading="loading"
        @cancel="closeDialog"
        @confirm="executeDelete"
      />
    </v-dialog>

    <card-table
      :items="items"
      :headers="headers"
      :loading="loading"
    >
      <template #action>
        <base-button @click="openCreate">
          <template #append><v-icon :icon="mdiPlus" /></template>
          <template #default>
            {{ t("common.add") }}
          </template>
        </base-button>
      </template>

      <template v-slot:[`item.actions`]="{ item }">
        <slot name="item.actions">
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
                type="delete"
                @click="promptDelete(item)"
              />
            </v-col>
          </v-row>
        </slot>
      </template>

      <template
        v-for="(_, slotName) in $slots"
        :key="slotName"
        v-slot:[slotName]="slotProps"
      >
        <slot
          v-if="slotName !== 'form' && slotName !== 'item.actions'"
          :name="slotName"
          v-bind="slotProps || {}"
        >
        </slot>
      </template>
    </card-table>
  </div>
</template>

<script setup lang="ts" generic="T extends { id?: string }">
import type { TableHeader } from "@/components/common/CardTable.vue";

import { mdiPlus } from "@mdi/js";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";

import ActionButton from "@/components/common/buttons/ActionButton.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CardTable from "@/components/common/CardTable.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";
import ViewSimpleHeader from "@/components/common/ViewSimpleHeader.vue";

const {
  loading = false,
  emptyItemTemplate,
  maxDialogWidth = "800px",
} = defineProps<{
  maxDialogWidth?: string;
  domain: string;
  items: readonly T[];
  headers: TableHeader<T>[];
  loading?: boolean;
  emptyItemTemplate: T;
}>();

const emit = defineEmits<{
  create: [item: T];
  update: [item: T];
  delete: [id: string];
}>();

const { t } = useI18n();

// --- State ---
type DialogMode = "form" | "delete" | null;
const dialogMode = ref<DialogMode>(null);
const showDialog = computed(() => dialogMode.value !== null);

const activeItem = ref<T>({ ...emptyItemTemplate } as T);

// --- Functions ---
const updateActiveItem = (newValue: T) => {
  activeItem.value = newValue;
};

const openCreate = () => {
  activeItem.value = { ...emptyItemTemplate } as T;
  dialogMode.value = "form";
};

const openEdit = (item: T) => {
  activeItem.value = { ...item } as T;
  dialogMode.value = "form";
};

const promptDelete = (item: T) => {
  activeItem.value = item;
  dialogMode.value = "delete";
};

const handleSave = () => {
  if (activeItem.value.id) {
    emit("update", activeItem.value);
  } else {
    emit("create", activeItem.value);
  }
};

const executeDelete = () => {
  if (activeItem.value?.id) {
    emit("delete", activeItem.value.id);
  }
};

const closeDialog = () => {
  dialogMode.value = null;
  activeItem.value = { ...emptyItemTemplate } as T;
};

defineExpose({
  closeDialog,
});
</script>

<style scoped></style>
