<template>
  <v-autocomplete
    v-model="modelValue"
    :label="
      hasOppositeTypeSelected
        ? type === InternalPersonRequestDtoTypeEnum.INTERNAL
          ? t('components.personSelect.coveredByExternal')
          : t('components.personSelect.coveredByInternal')
        : type === InternalPersonRequestDtoTypeEnum.INTERNAL
          ? t('components.personSelect.searchInternal')
          : t('components.personSelect.searchExternal')
    "
    :hint="
      hasOppositeTypeSelected
        ? type === InternalPersonRequestDtoTypeEnum.INTERNAL
          ? t('components.personSelect.externalAlreadySelectedHint')
          : t('components.personSelect.internalAlreadySelectedHint')
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

import { InternalPersonRequestDtoTypeEnum } from "@/api/raumreservierung-backend";
import { useGetPersonPage } from "@/composables/api/usePersonApi.ts";

const { type } = defineProps<{
  type: InternalPersonRequestDtoTypeEnum;
}>();

const { t } = useI18n();
const modelValue = defineModel<FindById200Response>();

const hasOppositeTypeSelected = computed(
  () => modelValue.value && modelValue.value.type !== type
);

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
    personType: type,
  });
}, 500);
</script>
