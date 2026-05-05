<template>
  <v-card
    :title="t('generics.edit', { domain })"
    :subtitle="t('components.roomResourceManagementCard.subtitle', { domain })"
    :loading="loading"
  >
    <template #text>
      <p v-if="!itemList">
        {{ t("components.roomResourceManagementCard.emptyList", { domain }) }}
      </p>
      <v-chip-group
        v-else
        column
        variant="outlined"
      >
        <v-chip
          v-for="item in itemList"
          :key="item.id"
          :text="item.name"
        />
      </v-chip-group>
      <v-divider class="my-4" />
      <v-text-field
        v-model="newItemName"
        :label="t('generics.add', { domain })"
        :disabled="loading"
        @keyup.enter="handleCreate"
      >
        <template #append-inner>
          <action-button
            type="add"
            color="accent"
            @click="handleCreate"
          />
        </template>
      </v-text-field>
    </template>
    <template #actions>
      <base-button
        class="mb-4 mr-4"
        secondary
        :prepend-icon="mdiArrowLeft"
        :text="t('common.back')"
        @click="emit('close')"
      />
    </template>
  </v-card>
</template>

<script setup lang="ts" generic="T extends { id?: string; name: string }">
import { mdiArrowLeft } from "@mdi/js";
import { ref } from "vue";
import { useI18n } from "vue-i18n";

import ActionButton from "@/components/common/buttons/ActionButton.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";

const { t } = useI18n();

const newItemName = ref("");

const { loading = false } = defineProps<{
  itemList: readonly T[] | null;
  domain: string;
  loading?: boolean;
}>();

const emit = defineEmits<{
  create: [name: string];
  close: [];
}>();

const handleCreate = async () => {
  emit("create", newItemName.value.trim());
  newItemName.value = "";
};
</script>

<style scoped></style>
