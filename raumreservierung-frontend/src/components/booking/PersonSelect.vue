<template>
  <v-autocomplete
    v-model="modelValue"
    :label="computedLabel"
    :hint="computedHint"
    persistent-hint
    color="accent"
    variant="outlined"
    clearable
    :prepend-inner-icon="mdiAccountSearchOutline"
    :items="foundPersons?.content ?? []"
    :loading="personPageLoading"
    item-value="id"
    hide-no-data
    no-filter
    :return-object="!!type"
    :disabled="hasOppositeTypeSelected"
    @update:search="onSearch"
  >
    <template #selection="{ item }">
      <!-- intialPerson is fallback if only id is present -->
      {{ formatName(item.firstName || item.lastName ? item : initialPerson) }}
      <span
        v-if="showEmail && selectionEmail(item)"
        class="text-grey ml-1"
      >
        {{ t("common.format.braces", { content: selectionEmail(item) }) }}
      </span>
    </template>
    <template #item="{ item, props }">
      <v-list-item
        v-bind="props"
        :title="formatName(item)"
        :subtitle="
          showEmail ? t('common.format.braces', { content: item.email }) : ''
        "
      >
      </v-list-item>
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
import {
  useFindPerson,
  useGetPersonPage,
} from "@/composables/api/usePersonApi.ts";

const {
  type,
  label,
  showEmail = false,
} = defineProps<{
  type?: InternalPersonRequestDtoTypeEnum;
  label?: string;
  showEmail?: boolean;
}>();

const { t } = useI18n();
const modelValue = defineModel<FindById200Response | string>();

const isPersonObject = (
  value: FindById200Response | string | undefined
): value is FindById200Response => typeof value === "object" && value !== null;

const hasOppositeTypeSelected = computed(
  () =>
    !!type && isPersonObject(modelValue.value) && modelValue.value.type !== type
);

const isInternal = computed(
  () => type === InternalPersonRequestDtoTypeEnum.INTERNAL
);

const typeLabel = (isInternal: boolean) =>
  isInternal
    ? t("components.personSelect.types.internalType")
    : t("components.personSelect.types.externalType");

const capitalize = (str: string) => str.charAt(0).toUpperCase() + str.slice(1);

const computedLabel = computed(
  () =>
    label ||
    (hasOppositeTypeSelected.value
      ? t("components.personSelect.coveredBy", {
          type: typeLabel(!isInternal.value),
        })
      : t("components.personSelect.search", {
          type: capitalize(typeLabel(isInternal.value)),
        }))
);

const computedHint = computed(() =>
  hasOppositeTypeSelected.value
    ? t("components.personSelect.alreadySelectedHint", {
        type: typeLabel(!isInternal.value),
      })
    : ""
);

const {
  call: getPersonPage,
  data: foundPersons,
  loading: personPageLoading,
} = useGetPersonPage();

const idForLookup = computed(() =>
  typeof modelValue.value === "string" ? modelValue.value : undefined
);

const { data: initialPerson } = useFindPerson(idForLookup);

const formatName = (person: FindById200Response | undefined) =>
  person
    ? t("common.format.fullName", {
        firstName: person.firstName,
        lastName: person.lastName,
      })
    : t("components.personSelect.personNotFound");

const selectionEmail = (item: FindById200Response) => {
  return item?.email ?? initialPerson.value?.email;
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
