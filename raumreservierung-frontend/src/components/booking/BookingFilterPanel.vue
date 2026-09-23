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
            xl="6"
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
            xl="3"
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
            xl="3"
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
              :icon="
                expanded
                  ? hiddenActiveFiltersCount > 0
                    ? mdiFilterMinus
                    : mdiFilterMinusOutline
                  : hiddenActiveFiltersCount > 0
                    ? mdiFilterPlus
                    : mdiFilterPlusOutline
              "
            />
          </v-badge>
        </template>
      </v-expansion-panel-title>
      <v-divider />
      <v-expansion-panel-text class="pt-2 pr-10">
        <v-row density="comfortable">
          <v-col
            cols="12"
            md="6"
          >
            <room-select
              v-model="roomId"
              :label="t('generics.filter', { domain: t('domain.room.header') })"
              :show-inactive="showInactiveRooms"
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
              :label="t('components.filterPanel.bookedFor')"
              density="compact"
              hide-details
              show-email
              :return-object="false"
              @update:model-value="onFiltersChanged"
            />
          </v-col>
          <v-col cols="12">
            <v-text-field
              v-model="titleInput"
              :label="t('domain.booking.bookingTitle')"
              color="accent"
              variant="outlined"
              density="compact"
              clearable
              hide-details
              :prepend-inner-icon="mdiTextBoxSearchOutline"
              @update:model-value="onTitleInput"
              @click:clear="onTitleInput(undefined)"
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
  mdiFilterMinus,
  mdiFilterMinusOutline,
  mdiFilterPlus,
  mdiFilterPlusOutline,
  mdiTextBoxSearchOutline,
} from "@mdi/js";
import { useDebounceFn } from "@vueuse/core";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";

import GeneralStatusSelect from "@/components/booking/GeneralStatusSelect.vue";
import PersonSelect from "@/components/booking/PersonSelect.vue";
import RoomSelect from "@/components/rooms/RoomSelect.vue";

const { t } = useI18n();

defineProps<{
  showInactiveRooms?: boolean;
  getStatusGroupKey?: (item: string) => StatusGroupKey | string;
}>();

const emit = defineEmits<{
  "updated:filters": [];
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
const titleInput = ref(title.value);

const onFiltersChanged = () => {
  emit("updated:filters");
};

const hiddenActiveFiltersCount = computed(
  () => [roomId.value, bookedForId.value, title.value].filter(Boolean).length
);

const onTitleInput = useDebounceFn((value: string | undefined) => {
  title.value = value || undefined;
  onFiltersChanged();
}, 500);
</script>

<style scoped></style>
