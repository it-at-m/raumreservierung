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
        :label="t('components.exportWeekScheduleCard.chooseWeek')"
        :min="MIN_WEEK"
        :max="MAX_WEEK"
        :rules="weekRules"
      />
      <v-select
        v-model="selectedBuilding"
        variant="outlined"
        hide-details
        :label="t('components.exportWeekScheduleCard.chooseCategory')"
        :items="buildingItems"
        item-title="title"
        item-value="value"
      />
    </template>
    <template #confirm="{ props }">
      <base-button
        :append-icon="mdiCalendarExportOutline"
        :disabled="!canExport"
        :text="t('components.exportWeekScheduleCard.confirm')"
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
import { useI18n } from "vue-i18n";

const router = useRouter();
const { t } = useI18n();
const adapter = useDate();

const emit = defineEmits<{
  close: [];
}>();

const MIN_WEEK = 1;
const MAX_WEEK = 53;
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
    title: t("domain.weekScheduleCategory.altesRathaus"),
  },
  {
    value: WeekScheduleAsPDFCategoryEnum.NEUES_RATHAUS,
    title: t("domain.weekScheduleCategory.altesRathaus"),
  },
]);

const canExport = computed(() => isWeekValid(selectedWeek.value));

const isWeekValid = (value: number | null): boolean =>
  value !== null &&
  Number.isInteger(value) &&
  value >= MIN_WEEK &&
  value <= MAX_WEEK;

const weekRules = [
  (value: number | null) => isWeekValid(value) || t("common.rules.invalidKW"),
];

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
