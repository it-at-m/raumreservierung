<template>
  <div>
    <view-simple-header :header-text="domain + 'Nutzerinnen verwalten'" />

    <v-text-field
      variant="outlined"
      label="Suche"
    />

    <v-dialog
      :model-value="showDialog"
      width="90%"
      :max-width="maxDialogWidth"
      persistent
      close-on-back
    >
      <confirm-card
        v-if="dialogMode == 'form'"
        :loading="loading"
        title="Erstellen und Löschen"
        @confirm="handleSave"
        @cancel="closeDialog"
      >
        <template #text>
          <slot
            name="form"
            :item="activeItem"
            :updateItem="updateActiveItem"
            :updateValidity="updateFormValidity"
          />
        </template>
        <template #confirm="{ props }">
          <base-button
            :text="t('common.save')"
            :append-icon="mdiContentSaveOutline"
            :disabled="!isFormSlotValid"
            v-bind="props"
          />
        </template>
      </confirm-card>

      <confirm-card
        v-else-if="dialogMode === 'delete'"
        :title="t('generics.delete', { domain })"
        :text="t('generics.confirmDelete', { domain })"
        :loading="loading"
        @cancel="closeDialog"
        @confirm="executeDelete"
      >
        <template #confirm="{ props }">
          <base-button
            :text="t('common.delete')"
            :append-icon="mdiTrashCanOutline"
            v-bind="props"
          />
        </template>
      </confirm-card>
    </v-dialog>

    <v-card>
      <template #title>
        <v-row align-content="center">
          <v-col class="d-flex align-center justify-end">
            <span> letztes Update: {{ now.getUTCDate() }} </span>
            <base-button
              class="ml-2 mr-2"
              secondary
              @click="openCreate"
              :append-icon="mdiUpdate"
              text="LDAP updaten"
            />
            <base-button
              @click="openCreate"
              :append-icon="mdiPlus"
              :text="t('common.add')"
            />
          </v-col>
        </v-row>
        <v-row class="mt-2">
          <v-col>
            <v-divider />
          </v-col>
        </v-row>
      </template>
      <template #text>
        <slot
          name="table"
          :openEdit="openEdit"
          :openDelete="promptDelete"
        />
      </template>
    </v-card>
  </div>
</template>

<script setup lang="ts" generic="T extends { id?: string }">
import type { TableHeader } from "@/components/common/CardTable.vue";

import {
  mdiContentSaveOutline,
  mdiPlus,
  mdiTrashCanOutline,
  mdiUpdate,
} from "@mdi/js";
import { useNow } from "@vueuse/core";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";

import BaseButton from "@/components/common/buttons/BaseButton.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";
import ViewSimpleHeader from "@/components/common/ViewSimpleHeader.vue";

const { t } = useI18n();

const now = useNow();

type DialogMode = "form" | "delete" | null;
const dialogMode = ref<DialogMode>(null);
const showDialog = computed(() => dialogMode.value !== null);

const {
  maxDialogWidth = "800px",
  emptyItemTemplate,
  loading = false,
} = defineProps<{
  items: readonly T[];
  itemsLength: number;
  maxDialogWidth?: string;
  emptyItemTemplate: T;
  headers: TableHeader<T>[];
  domain: string;
  loading?: boolean;
}>();

const activeItem = ref<T>({ ...emptyItemTemplate } as T);

const isFormSlotValid = ref(false);

const emit = defineEmits<{
  create: [item: T];
  update: [item: T];
  delete: [id: string];
  // Sadly there is no type for the emit of updatedOptions ...
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  "update:options": [options: any];
}>();

// --- Functions ---
const updateFormValidity = (valid: boolean | null) => {
  isFormSlotValid.value = !!valid;
};

const updateActiveItem = (newValue: T) => {
  activeItem.value = newValue;
};

const openCreate = () => {
  activeItem.value = { ...emptyItemTemplate } as T;
  dialogMode.value = "form";
};

const openEdit = (item: T) => {
  activeItem.value = JSON.parse(JSON.stringify(item)) as T;
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
