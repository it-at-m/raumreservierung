<template>
  <v-card
    flat
    :subtitle="t('components.scheduleTimelineCard.header')"
  >
    <template #text>
      <v-timeline
        class="mx-n4 mb-n4"
        direction="vertical"
        density="comfortable"
      >
        <v-timeline-item
          dot-color="accent"
          size="x-small"
        >
          <span class="font-weight-bold me-2">
            {{ useDateFormat(schedule?.occupancyStart, TIME_FORMAT_HHMM) }}
          </span>
          <span>{{ t("components.scheduleTimelineCard.start") }}</span>
        </v-timeline-item>

        <v-timeline-item
          v-if="schedule?.appointmentStart && schedule?.appointmentEnd"
          dot-color="tertiary"
          size="small"
        >
          <span class="font-weight-bold me-2">
            {{
              t("common.format.dateRange", {
                start: useDateFormat(
                  schedule?.appointmentStart,
                  TIME_FORMAT_HHMM
                ).value,
                end: useDateFormat(schedule?.appointmentEnd, TIME_FORMAT_HHMM)
                  .value,
              })
            }}
          </span>
          <span> {{ t("components.scheduleTimelineCard.event") }} </span>
        </v-timeline-item>

        <v-timeline-item
          dot-color="accent"
          size="x-small"
        >
          <span class="font-weight-bold me-2">
            {{ useDateFormat(schedule?.occupancyEnd, TIME_FORMAT_HHMM) }}
          </span>
          <span>{{ t("components.scheduleTimelineCard.end") }}</span>
        </v-timeline-item>
      </v-timeline>
    </template>
  </v-card>
</template>

<script setup lang="ts">
import type { ScheduleTemplate } from "@/api/raumreservierung-backend";

import { useDateFormat } from "@vueuse/core";
import { useI18n } from "vue-i18n";

import { TIME_FORMAT_HHMM } from "@/constants.ts";

const { t } = useI18n();

defineProps<{
  schedule?: ScheduleTemplate;
}>();
</script>

<style scoped></style>
