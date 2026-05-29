<template>
  <v-select
    v-model="modelValue"
    v-bind="$attrs"
    :prepend-inner-icon="mdiSofaSingleOutline"
    :loading="getSeatingTypeLoading || loading"
    :items="allSeatingTypes ?? []"
    item-value="id"
    item-title="name"
    label="Bestuhlungsart auswählen"
    variant="outlined"
    :disabled="getSeatingTypeLoading || loading"
    hide-details
  />
</template>

<script setup lang="ts">
import { mdiSofaSingleOutline } from "@mdi/js";
import { onMounted } from "vue";

import { useGetAllSeatingTypes } from "@/composables/api/useSeatingApi.ts";

const modelValue = defineModel<string>();

defineProps<{
  loading?: boolean;
}>();

const {
  call: getSeatingTypes,
  data: allSeatingTypes,
  loading: getSeatingTypeLoading,
} = useGetAllSeatingTypes();

onMounted(async () => {
  await getSeatingTypes();
});
</script>

<style scoped></style>
