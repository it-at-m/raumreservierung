<template>
  <v-expansion-panels>
    <v-expansion-panel>
      <v-expansion-panel-title>
        <v-row
          density="comfortable"
          class="mr-4"
          @click.stop
        >
          <v-col
            cols="12"
            md="6"
          >
            <general-status-select
              v-model="statusFilter"
              density="compact"
              clearable
              :label="t('domain.booking.status.filter')"
              multiple
              :group-by="getStatusGroupKey"
              @update:model-value="onFiltersChanged"
            />
          </v-col>
          <v-col
            cols="12"
            sm="6"
            md="3"
          >
            <v-date-input
              v-model="start"
              :label="t('views.bookingListView.periodFrom')"
              density="compact"
              variant="outlined"
              prepend-icon=""
              :prepend-inner-icon="mdiCalendarStartOutline"
              clearable
              hide-details
              @update:model-value="onFiltersChanged"
            />
          </v-col>
          <v-col
            cols="12"
            sm="6"
            md="3"
          >
            <v-date-input
              v-model="end"
              prepend-icon=""
              :prepend-inner-icon="mdiCalendarEndOutline"
              :label="t('views.bookingListView.periodTo')"
              density="compact"
              variant="outlined"
              clearable
              hide-details
              @update:model-value="onFiltersChanged"
            />
          </v-col>
        </v-row>
        <template #actions="{ expanded }">
          <v-badge
            :content="hiddenActiveFiltersCount"
            :model-value="hiddenActiveFiltersCount > 0"
            color="accent"
            offset-x="-2"
          >
            <v-icon
              :icon="expanded ? mdiFilterOutline : mdiFilterMenuOutline"
            />
          </v-badge>
        </template>
      </v-expansion-panel-title>
      <v-expansion-panel-text>
        <v-row density="comfortable">
          <v-col
            cols="12"
            md="6"
          >
            <room-select
              v-model="roomId"
              :label="t('generics.filter', { domain: t('domain.room.header') })"
              :show-inactive="canEditBookings"
              density="compact"
              clearable
              @update:model-value="onFiltersChanged"
            />
          </v-col>
          <v-col
            cols="12"
            md="6"
          >
            <person-select
              v-model="bookedForId"
              :label="t('views.bookingDetailsView.bookedFor')"
              density="compact"
              hide-details
              hide-menu-icon
              show-email
              :return-object="false"
              @update:model-value="onFiltersChanged"
            />
          </v-col>
          <v-col cols="12">
            <title-select
              v-model="title"
              @update:model-value="onFiltersChanged"
            />
          </v-col>
        </v-row>
      </v-expansion-panel-text>
    </v-expansion-panel>
  </v-expansion-panels>
</template>
<script setup lang="ts">
import type { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend";
import type { StatusGroupKey } from "@/constants/BookingStatus.ts";

import {
  mdiCalendarEndOutline,
  mdiCalendarStartOutline,
  mdiFilterMenuOutline,
  mdiFilterOutline,
} from "@mdi/js";
import { computed } from "vue";
import { useI18n } from "vue-i18n";

import GeneralStatusSelect from "@/components/booking/GeneralStatusSelect.vue";
import PersonSelect from "@/components/booking/PersonSelect.vue";
import TitleSelect from "@/components/booking/TitleSelect.vue";
import RoomSelect from "@/components/rooms/RoomSelect.vue";

const { t } = useI18n();

defineProps<{
  canEditBookings?: boolean;
  getStatusGroupKey?: (item: string) => StatusGroupKey | string;
}>();

const emit = defineEmits<{
  "apply-filters": [];
}>();

const roomId = defineModel<string>("roomId");
const statusFilter = defineModel<BookingStatusDTOCurrentStatusEnum[]>(
  "statusFilter",
  {
    default: () => [],
  }
);
const start = defineModel<Date | null>("start");
const end = defineModel<Date | null>("end");
const bookedForId = defineModel<string>("bookedForId");
const title = defineModel<string>("title");

const onFiltersChanged = () => {
  emit("apply-filters");
};

const hiddenActiveFiltersCount = computed(
  () => [roomId.value, bookedForId.value, title.value].filter(Boolean).length
);
</script>

<style scoped></style>
