<template>
  <confirm-card
    :title="t('components.exportWeekScheduleCard.confirm')"
    @confirm="emit('close')"
    @cancel="emit('close')"
  >
    <template #text>
      <v-field
        variant="outlined"
        active
        class="mb-4"
        :label="t('components.exportWeekScheduleCard.chooseWeek')"
        :focused="focused"
        @focusin="focused = true"
        @focusout="focused = false"
      >
        <v-date-picker
          :model-value="weekDays"
          multiple
          color="primary"
          width="100%"
          hide-header
          show-adjacent-months
          show-week
          weekday-format="short"
          :first-day-of-week="MONDAY"
          @update:model-value="onSelect"
        />
      </v-field>
      <v-select
        v-model="selectedBuilding"
        variant="outlined"
        hide-details
        :label="t('components.exportWeekScheduleCard.chooseCategory')"
        :items="buildingItems"
      />
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
const DAYS_IN_WEEK = 7;

const selectedDate = ref<Date>(new Date());
const selectedBuilding = ref<WeekScheduleAsPDFCategoryEnum | null>(null);
const focused = ref(false);

const buildingItems = computed(() =>
  Object.entries(weekScheduleCategoryMap.value).map(([value, title]) => ({
    value,
    title,
  }))
);

const canExport = computed(() => selectedBuilding.value !== null);

const redirectToDownload = async () => {
  const category = selectedBuilding.value;
  if (!category) {
    return;
  }
  const exportApi = ApiFactory.getInstance(ExportControllerApi);
  const params = {
    date: selectedDate.value,
    category,
  };

  const requestOpts = await exportApi.weekScheduleAsPDFRequestOpts(params);

  const routeData = router.resolve({
    path: `${BASE_PATH}${requestOpts.path}`,
    query: {
      date: adapter.toISO(selectedDate.value),
      category,
    },
  });

  window.open(routeData.href, "_blank");
};

const weekDays = computed<Date[]>(() => {
  const selected = selectedDate.value;
  const monday = adapter.startOfWeek(selected, MONDAY);
  return Array.from({ length: DAYS_IN_WEEK }, (_, i) =>
    adapter.toJsDate(adapter.addDays(monday, DAYS_IN_WEEK - 1 - i))
  );
});

const isIn = (days: Date[], day: Date) =>
  days.some((d) => adapter.isSameDay(d, day));

const onSelect = (value: Date[]) => {
  const clicked = value.find((day) => !isIn(weekDays.value, day));

  if (clicked) {
    selectedDate.value = clicked;
  }
};
</script>

<style scoped></style>
