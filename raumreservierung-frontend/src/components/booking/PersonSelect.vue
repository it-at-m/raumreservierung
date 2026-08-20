<template>
  <v-autocomplete
    v-model="modelValue"
    :label="computedLabel"
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
    :density="density"
    :hide-details="hideDetails"
    :prepend-inner-icon="mdiAccountSearchOutline"
    :items="foundPersons?.content ?? []"
    :loading="personPageLoading"
    :item-title="formatName"
    item-value="id"
    hide-no-data
    :no-filter="!type"
    :return-object="!!type"
    :menu-icon="hideMenuIcon ? '' : undefined"
    :disabled="hasOppositeTypeSelected"
    @update:search="onSearch"
  >
    <template #selection="{ item }">
      {{ selectionLabel(item) }}
      <span
        v-if="showEmail && selectionEmail(item)"
        class="text-grey ml-1"
      >
        {{ t("common.format.braces", { content: selectionEmail(item) }) }}
      </span>
    </template>
    <template
      v-if="showEmail"
      #item="{ item, props }"
    >
      <v-list-item
        v-bind="props"
        :title="undefined"
      >
        {{ formatName(item) }}
        <span
          v-if="item.email"
          class="text-grey"
        >
          {{ t("common.format.braces", { content: item.email }) }}
        </span>
      </v-list-item>
    </template>
  </v-autocomplete>
</template>

<script
  setup
  lang="ts"
  generic="
    Type extends InternalPersonRequestDtoTypeEnum | undefined = undefined
  "
>
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
  density = "default",
  hideDetails = false,
  hideMenuIcon = false,
  showEmail = false,
} = defineProps<{
  type?: InternalPersonRequestDtoTypeEnum;
  label?: string;
  density?: "compact" | "default";
  hideDetails?: boolean;
  hideMenuIcon?: boolean;
  showEmail?: boolean;
}>();

const { t } = useI18n();
const modelValue =
  defineModel<Type extends undefined ? string : FindById200Response>();

const isPersonObject = (
  value: FindById200Response | string | undefined
): value is FindById200Response => typeof value === "object" && value !== null;

const hasOppositeTypeSelected = computed(
  () =>
    !!type && isPersonObject(modelValue.value) && modelValue.value.type !== type
);

const computedLabel = computed(() => {
  if (label) {
    return label;
  }
  return hasOppositeTypeSelected.value
    ? type === InternalPersonRequestDtoTypeEnum.INTERNAL
      ? t("components.personSelect.coveredByExternal")
      : t("components.personSelect.coveredByInternal")
    : type === InternalPersonRequestDtoTypeEnum.INTERNAL
      ? t("components.personSelect.searchInternal")
      : t("components.personSelect.searchExternal");
});

const {
  call: getPersonPage,
  data: foundPersons,
  loading: personPageLoading,
} = useGetPersonPage();

const idForLookup = computed(() =>
  typeof modelValue.value === "string" ? modelValue.value : undefined
);

const { data: initialPerson } = useFindPerson(idForLookup);

const selectionLabel = (item: FindById200Response) => {
  if (item?.firstName || item?.lastName) {
    return formatName(item);
  }
  return formatName(initialPerson.value);
};

const selectionEmail = (item: FindById200Response) => {
  return item?.email ?? initialPerson.value?.email;
};

const formatName = (person: FindById200Response | undefined) => {
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
