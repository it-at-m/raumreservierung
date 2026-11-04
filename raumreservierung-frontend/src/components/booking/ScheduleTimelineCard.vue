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
          <i18n-d
            v-if="schedule"
            :value="schedule?.occupancyStart"
            format="time"
            class="font-weight-bold me-2"
            tag="span"
          />
          <span>{{ t("components.scheduleTimelineCard.start") }}</span>
        </v-timeline-item>

        <v-timeline-item
          v-if="schedule?.appointmentStart && schedule?.appointmentEnd"
          dot-color="tertiary"
          size="small"
        >
          <i18n-t
            v-if="schedule"
            keypath="common.format.dateRange"
            scope="global"
            tag="span"
            class="font-weight-bold me-2"
          >
            <template #start>
              <i18n-d
                :value="schedule.appointmentStart"
                format="time"
              />
            </template>
            <template #end>
              <i18n-d
                :value="schedule.appointmentEnd"
                format="time"
              />
            </template>
          </i18n-t>
          <span> {{ t("components.scheduleTimelineCard.event") }} </span>
        </v-timeline-item>

        <v-timeline-item
          dot-color="accent"
          size="x-small"
        >
          <i18n-d
            v-if="schedule"
            :value="schedule?.occupancyEnd"
            format="time"
            class="font-weight-bold me-2"
            tag="span"
          />
          <span>{{ t("components.scheduleTimelineCard.end") }}</span>
        </v-timeline-item>
      </v-timeline>
    </template>
  </v-card>
</template>

<script setup lang="ts">
import type { ScheduleTemplate } from "@/api/raumreservierung-backend";

import { useI18n } from "vue-i18n";

const { t } = useI18n();

defineProps<{
  schedule?: ScheduleTemplate;
}>();
</script>

<style scoped></style>
