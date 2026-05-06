<template>
  <div>
    <v-dialog
      :model-value="showDialog"
      width="90%"
      :max-width="maxDialogWidth"
      persistent
      close-on-back
    >
      <confirm-card
        v-if="dialogMode == 'read' || dialogMode == 'edit'"
        :loading="loading"
        :title="
          dialogMode == 'read'
            ? domain
            : t(activeItem.id ? 'generics.edit' : 'generics.create', {
                domain: domain,
              })
        "
        @confirm="handleSave"
        @cancel="closeDialog"
      >
        <template #text>
          <slot
            name="form"
            :item="activeItem"
            :update-item="updateActiveItem"
            :update-validity="updateFormValidity"
            :read-only="dialogMode == 'read'"
          />
        </template>
        <template #cancel="{ props }">
          <base-button
            v-if="dialogMode == 'read'"
            v-bind="props"
            secondary
            :text="t('common.close')"
            :prepend-icon="mdiClose"
          />
        </template>
        <template #confirm="{ props }">
          <base-button
            v-if="dialogMode == 'edit'"
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

    <v-card :loading="loading">
      <template #title>
        <v-row align-content="center">
          <v-col class="d-flex align-center justify-end">
            <slot
              name="tableActions"
              :open-create="openCreate"
            >
              <base-button
                :append-icon="mdiPlus"
                :text="t('common.add')"
                @click="openCreate"
              />
            </slot>
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
          :open-edit="openEdit"
          :open-delete="promptDelete"
          :open-read-only="openRead"
        />
      </template>
    </v-card>
  </div>
</template>

<script setup lang="ts" generic="T extends { id?: string }">
import {
  mdiClose,
  mdiContentSaveOutline,
  mdiPlus,
  mdiTrashCanOutline,
} from "@mdi/js";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";

import BaseButton from "@/components/common/buttons/BaseButton.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";

const { t } = useI18n();

type DialogMode = "edit" | "delete" | "read" | null;
const dialogMode = ref<DialogMode>(null);
const showDialog = computed(() => dialogMode.value !== null);

const {
  maxDialogWidth = "800px",
  emptyItemTemplate,
  loading = false,
} = defineProps<{
  maxDialogWidth?: string;
  emptyItemTemplate: T;
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
  dialogMode.value = "edit";
};

const openEdit = (item: T) => {
  activeItem.value = JSON.parse(JSON.stringify(item)) as T;
  dialogMode.value = "edit";
};

const openRead = (item: T) => {
  activeItem.value = JSON.parse(JSON.stringify(item)) as T;
  dialogMode.value = "read";
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
  openReadOnly: openRead,
});
</script>

<style scoped></style>
