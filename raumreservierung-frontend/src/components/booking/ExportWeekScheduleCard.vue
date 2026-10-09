<template>
  <confirm-card
    title="Wähle Woche"
    @confirm="emit('close')"
    @cancel="emit('close')"
  >
    <template #text>
      <v-text-field
        v-model.number="selectedWeek"
        class="mb-4"
        type="number"
        variant="outlined"
        hide-details="auto"
        label="Kalenderwoche wählen"
        :min="MIN_WEEK"
        :max="MAX_WEEK"
        :rules="isWeekValid ? [] : ['KW ungültig']"
      />
      <v-select
        v-model="selectedBuilding"
        variant="outlined"
        hide-details
        label="Kategorie auswählen"
        :items="buildingItems"
        item-title="title"
        item-value="value"
      />
    </template>
    <template #confirm="{ props }">
      <base-button
        :append-icon="mdiCalendarExportOutline"
        :disabled="!selectedWeek"
        text="Dienstplan exportieren"
        @click="
          redirectToDownload();
          props.onClick();
        "
      />
    </template>
  </confirm-card>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useRouter } from "vue-router";

import {
  ExportControllerApi,
  WeekScheduleAsPDFCategoryEnum,
} from "@/api/raumreservierung-backend";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";
import { BASE_PATH } from "@/constants.ts";
import { ApiFactory } from "@/util/apiFactory.ts";
import { mdiCalendarExportOutline } from "@mdi/js";
import { useDate } from "vuetify/framework";

const router = useRouter();

const MIN_WEEK = 1;
const MAX_WEEK = 52;

const emit = defineEmits<{
  close: [];
}>();

const adapter = useDate();

const MONDAY = 1;
const ISO_FIRST_WEEK_DAY = 4;

const selectedWeek = ref<number | null>(
  adapter.getWeek(new Date(), MONDAY, ISO_FIRST_WEEK_DAY)
);

const selectedBuilding = ref<WeekScheduleAsPDFCategoryEnum>(
  WeekScheduleAsPDFCategoryEnum.ALTES_RATHAUS
);

const buildingItems = computed(() => [
  {
    value: WeekScheduleAsPDFCategoryEnum.ALTES_RATHAUS,
    title: "Altes Rathaus",
  },
  {
    value: WeekScheduleAsPDFCategoryEnum.NEUES_RATHAUS,
    title: "Neues Rathaus",
  },
]);

const isWeekValid = computed(
  () =>
    selectedWeek.value &&
    Number.isInteger(selectedWeek.value) &&
    selectedWeek.value >= MIN_WEEK &&
    selectedWeek.value <= MAX_WEEK
);

const redirectToDownload = async () => {
  const exportApi = ApiFactory.getInstance(ExportControllerApi);
  const params = {
    week: selectedWeek.value ?? undefined,
    category: selectedBuilding.value,
  };

  const requestOpts = await exportApi.weekScheduleAsPDFRequestOpts(params);

  const routeData = router.resolve({
    path: `${BASE_PATH}${requestOpts.path}`,
    query: params,
  });

  window.open(routeData.href, "_blank");
};
</script>

<style scoped></style>
