<template>
  <v-autocomplete
    v-model="modelValue"
    label="Gebucht für"
    color="accent"
    variant="outlined"
    density="compact"
    clearable
    :prepend-inner-icon="mdiAccountSearchOutline"
    :items="combinedPersons"
    :loading="internalLoading || externalLoading"
    :item-title="formatName"
    menu-icon=""
    item-value="id"
    hide-no-data
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

const modelValue = defineModel<string>();

const {
  call: getInternalPersonPage,
  data: internalPersons,
  loading: internalLoading,
} = useGetPersonPage();

const {
  call: getExternalPersonPage,
  data: externalPersons,
  loading: externalLoading,
} = useGetPersonPage();

const combinedPersons = computed(() => [
  ...(internalPersons.value?.content ?? []).map((p) => ({
    ...p,
    personType: "INTERNAL",
  })),
  ...(externalPersons.value?.content ?? []).map((p) => ({
    ...p,
    personType: "EXTERNAL",
  })),
]);

const formatName = (person: FindById200Response & { personType?: string }) => {
  if (!person) {
    return "";
  }

  const name = `${person.firstName || ""} ${person.lastName || ""}`.trim();
  const label = person.personType === "INTERNAL" ? "Intern" : "Extern";

  return name ? `${name} (${label})` : "";
};

const onSearch = useDebounceFn((searchQuery: string) => {
  if (!searchQuery) {
    return;
  }

  getInternalPersonPage({
    searchName: searchQuery,
    page: 0,
    size: 10,
    personType: "INTERNAL",
  });

  getExternalPersonPage({
    searchName: searchQuery,
    page: 0,
    size: 10,
    personType: "EXTERNAL",
  });
}, 500);
</script>

<style scoped></style>
