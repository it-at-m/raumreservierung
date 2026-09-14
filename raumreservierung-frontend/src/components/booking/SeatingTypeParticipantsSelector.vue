<template>
  <v-select
    v-model="modelValue"
    v-bind="$attrs"
    :prepend-inner-icon="mdiSofaSingleOutline"
    :loading="getSeatingTypeLoading || loading"
    :items="filteredSeatingTypes ?? []"
    item-value="id"
    item-title="name"
    :label="t('generics.select', { domain: t('domain.seatingType.header') })"
    variant="outlined"
    :disabled="getSeatingTypeLoading || loading"
    hide-details
    :suffix="
      t('components.seatingTypeParticipantsSelector.suffix', {
        count: infoMaxCapacity,
      })
    "
  />
</template>

<script setup lang="ts">
import { mdiSofaSingleOutline } from "@mdi/js";
import { computed } from "vue";
import { useI18n } from "vue-i18n";

import { useGetAllSeatingTypes } from "@/composables/api/useSeatingApi.ts";

const { t } = useI18n();
const modelValue = defineModel<string>();

const { allowedIds = [] } = defineProps<{
  loading?: boolean;
  allowedIds?: string[] | undefined;
  infoMaxCapacity?: number;
}>();
//TODO combine seatingType cap with seatingType from room -> transfer whole unfiltered list!

const filteredSeatingTypes = computed(() =>
  allSeatingTypes?.value
    ? !allowedIds || allowedIds.length === 0
      ? allSeatingTypes.value
      : allSeatingTypes.value?.filter(
          (seatingType) =>
            seatingType.isActive &&
            seatingType.id &&
            allowedIds.includes(seatingType.id)
        )
    : []
);

const { data: allSeatingTypes, isPending: getSeatingTypeLoading } =
  useGetAllSeatingTypes();
</script>

<style scoped></style>
