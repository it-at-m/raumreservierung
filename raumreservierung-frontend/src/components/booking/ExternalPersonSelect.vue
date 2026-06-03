<template>
  <v-autocomplete
    v-model="modelValue"
    :label="
      isInternalSelected
        ? t('components.externalPersonSelect.coveredByInternal')
        : t('components.externalPersonSelect.searchExternal')
    "
    :hint="
      isInternalSelected
        ? t('components.externalPersonSelect.internalAlreadySelectedHint')
        : ''
    "
    persistent-hint
    color="accent"
    variant="outlined"
    clearable
    :prepend-inner-icon="mdiAccountSearchOutline"
    :items="foundPersons?.content ?? []"
    :loading="personPageLoading"
    :item-title="formatName"
    item-value="id"
    return-object
    hide-no-data
    :disabled="isInternalSelected"
    @update:search="onSearch"
  >
    <template #selection="{ item }">
      <span class="text-body-1">{{ formatName(item) }}</span>
    </template>
  </v-autocomplete>
</template>
<script setup lang="ts">
import type { FindById200Response } from "@/api/raumreservierung-backend";

import { mdiAccountSearchOutline } from "@mdi/js";
import { useDebounceFn } from "@vueuse/core";
import { computed } from "vue";

import { useGetPersonPage } from "@/composables/api/usePersonApi.ts";

const modelValue = defineModel<FindById200Response>();

const isInternalSelected = computed(() => {
  return modelValue.value && modelValue.value.type === "INTERNAL";
});

const {
  call: getPersonPage,
  data: foundPersons,
  loading: personPageLoading,
} = useGetPersonPage();

const formatName = (person: FindById200Response) => {
  if (!person) {
    return "";
  }
  return `${person.firstName || ""} ${person.lastName || ""}`.trim();
};

const onSearch = useDebounceFn((searchQuery: string) => {
  if (!searchQuery) {
    return;
  }

  getPersonPage({
    searchName: searchQuery,
    page: 0,
    size: 10,
    personType: "EXTERNAL",
  });
}, 500);
</script>

<style scoped></style>
