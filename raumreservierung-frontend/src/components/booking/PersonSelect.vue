<template>
  <v-autocomplete
    v-model="modelValue"
    :label="
      hasOppositeTypeSelected
        ? localization.labelCoveredBy
        : localization.labelSearch
    "
    :hint="hasOppositeTypeSelected ? localization.hintAlreadySelected : ''"
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
    :disabled="hasOppositeTypeSelected"
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
import { useI18n } from "vue-i18n";

import { useGetPersonPage } from "@/composables/api/usePersonApi.ts";

const props = defineProps<{
  type: "INTERNAL" | "EXTERNAL";
}>();

const { t } = useI18n();
const modelValue = defineModel<FindById200Response>();

const localization = computed(() => {
  if (props.type === "INTERNAL") {
    return {
      labelSearch: t("components.personSelect.searchInternal"),
      labelCoveredBy: t("components.personSelect.coveredByExternal"),
      hintAlreadySelected: t(
        "components.personSelect.externalAlreadySelectedHint"
      ),
    } as const;
  }

  return {
    labelSearch: t("components.personSelect.searchExternal"),
    labelCoveredBy: t("components.personSelect.coveredByInternal"),
    hintAlreadySelected: t(
      "components.personSelect.internalAlreadySelectedHint"
    ),
  } as const;
});

const hasOppositeTypeSelected = computed(() => {
  return modelValue.value && modelValue.value.type !== props.type;
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
    personType: props.type,
  });
}, 500);
</script>
