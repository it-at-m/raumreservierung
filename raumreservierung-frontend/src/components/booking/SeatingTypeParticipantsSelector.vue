<template>
  <v-select
    v-model="modelValue"
    v-bind="$attrs"
    :prepend-inner-icon="mdiSofaSingleOutline"
    :loading="getSeatingTypeLoading || loading"
    :items="itemsWithCapacity"
    item-value="id"
    :label="t('generics.select', { domain: t('domain.seatingType.header') })"
    variant="outlined"
    :disabled="getSeatingTypeLoading || loading"
    hide-details
    :suffix="
      t('components.seatingTypeParticipantsSelector.roomMaxCap', {
        count: infoMaxCapacity,
      })
    "
  >
    <template #item="{ props: itemProps, item }">
      <v-list-item v-bind="itemProps">
        <template #title>
          {{ item.title }}
          <span class="text-medium-emphasis">{{
            t("common.format.braces", {
              content: t(
                "components.seatingTypeParticipantsSelector.seatingMaxCap",
                {
                  count: item.capacity,
                }
              ),
            })
          }}</span>
        </template>
      </v-list-item>
    </template>
  </v-select>
</template>

<script setup lang="ts">
import { mdiSofaSingleOutline } from "@mdi/js";
import { computed } from "vue";
import { useI18n } from "vue-i18n";

import { useGetAllSeatingTypes } from "@/composables/api/useSeatingApi.ts";
import type { SeatingCapacityRequestDTO } from "@/api/raumreservierung-backend";

const { t } = useI18n();
const modelValue = defineModel<string>();

const { roomSeatingCapacities = [] } = defineProps<{
  loading?: boolean;
  roomSeatingCapacities?: SeatingCapacityRequestDTO[] | undefined;
  infoMaxCapacity?: number;
}>();

const capacityBySeatingTypeId = computed(
  () =>
    new Map(
      (roomSeatingCapacities ?? []).map((rsc) => [
        rsc.seatingTypeId,
        rsc.capacity,
      ])
    )
);

const filteredSeatingTypes = computed(() =>
  (allSeatingTypes.value ?? []).filter(
    (seatingType) =>
      seatingType.isActive &&
      seatingType.id &&
      capacityBySeatingTypeId.value.has(seatingType.id)
  )
);

const itemsWithCapacity = computed(() =>
  filteredSeatingTypes.value.map((seatingType) => ({
    ...seatingType,
    title: seatingType.name,
    capacity: seatingType.id
      ? capacityBySeatingTypeId.value.get(seatingType.id)
      : undefined,
  }))
);

const { data: allSeatingTypes, isPending: getSeatingTypeLoading } =
  useGetAllSeatingTypes();
</script>

<style scoped></style>
