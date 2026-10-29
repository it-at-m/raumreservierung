<template>
  <confirm-card
    :title="t('components.exportWeekScheduleCard.confirm')"
    @confirm="emit('close')"
    @cancel="emit('close')"
  >
    <template #text>
      <v-select
        v-model="selectedCategory"
        class="mb-4"
        variant="outlined"
        hide-details
        :label="t('components.exportWeekScheduleCard.chooseCategory')"
        :items="categoryItems"
      />
      <v-field
        variant="outlined"
        active
        :label="t('components.exportWeekScheduleCard.chooseWeek')"
      >
        <v-date-picker
          :model-value="weekRange"
          multiple="range"
          width="100%"
          hide-header
          show-adjacent-months
          show-week
          weekday-format="short"
          :first-day-of-week="MONDAY"
          @update:model-value="onSelect"
        />
      </v-field>
    </template>
    <template #confirm="{ props }">
      <base-button
        :append-icon="mdiCalendarExportOutline"
        :text="t('components.exportWeekScheduleCard.confirm')"
        :disabled="!canExport"
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
import { useWeekScheduleCategory } from "@/composables/useWeekScheduleCategory.ts";

const router = useRouter();
const { t } = useI18n();
const adapter = useDate();
const { weekScheduleCategoryMap } = useWeekScheduleCategory();

const emit = defineEmits<{
  close: [];
}>();

const MONDAY = 1;

const selectedDate = ref<Date>(new Date());
const selectedCategory = ref<WeekScheduleAsPDFCategoryEnum | null>(null);

const categoryItems = computed(() =>
  Object.entries(weekScheduleCategoryMap.value).map(([value, title]) => ({
    value,
    title,
  }))
);

const canExport = computed(() => selectedCategory.value !== null);

const redirectToDownload = async () => {
  if (!selectedCategory.value) {
    return;
  }
  const exportApi = ApiFactory.getInstance(ExportControllerApi);
  const params = {
    date: selectedDate.value,
    category: selectedCategory.value,
  };

  const requestOpts = await exportApi.weekScheduleAsPDFRequestOpts(params);

  const routeData = router.resolve({
    path: `${BASE_PATH}${requestOpts.path}`,
    query: {
      date: adapter.toISO(selectedDate.value),
      category: selectedCategory.value,
    },
  });

  window.open(routeData.href, "_blank");
};

const weekRange = computed<Date[]>(() => {
  const monday = adapter.startOfWeek(selectedDate.value, MONDAY);
  const sunday = adapter.addDays(monday, 6);
  return [adapter.toJsDate(monday), adapter.toJsDate(sunday)];
});

const onSelect = (value: unknown) => {
  const dates = Array.isArray(value) ? value : [value];
  const clicked = dates[dates.length - 1];

  if (clicked instanceof Date) {
    selectedDate.value = clicked;
  }
};
</script>

<style scoped>
:deep(.v-date-picker-month__day-btn.v-btn--variant-flat) {
  background-color: transparent !important;
  color: rgb(var(--v-theme-on-surface)) !important;
}
</style>
