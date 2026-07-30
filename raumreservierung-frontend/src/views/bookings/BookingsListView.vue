<template>
  <base-view
    :header-text="
      t('generics.manage', { domain: t('domain.booking.header', { count: 2 }) })
    "
  >
    <template #default>
      <v-sheet
        class="mb-6"
        rounded
      >
        <v-row>
          <v-col
            cols="12"
            md="4"
          >
            <room-select
              v-model="roomId"
              :label="t('generics.filter', { domain: t('domain.room.header') })"
              :show-inactive="canEditBookings"
              density="compact"
              clearable
              @update:model-value="applyFilters"
            />
          </v-col>
          <v-col
            cols="12"
            md="4"
          >
            <general-status-select
              v-model="statusFilter"
              density="compact"
              clearable
              :label="t('domain.booking.status.filter')"
              multiple
              @update:model-value="applyFilters"
            />
          </v-col>
          <v-col
            cols="12"
            sm="6"
            md="2"
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
              @update:model-value="applyFilters"
            />
          </v-col>
          <v-col
            cols="12"
            sm="6"
            md="2"
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
              @update:model-value="applyFilters"
            />
          </v-col>
        </v-row>
      </v-sheet>
      <v-card :title="t('views.bookingListView.tableTitle')">
        <template #text>
          <v-data-table-server
            v-model:sort-by="sortBy"
            v-model:page="page"
            v-model:items-per-page="itemsPerPage"
            :headers="headers"
            :items="bookingsPage?.content || []"
            :items-length="bookingsPage?.page?.totalElements || 0"
            :loading="getBookingsLoading"
            @update:options="displayOptions"
            @click:row="handleRowClick"
          >
            <template #[`item.status`]="{ item }">
              <status-chip
                :status="item.status?.currentStatus"
                variant="text"
              />
            </template>
            <template #[`item.hasEquipment`]="{ item }">
              <v-icon :icon="item.hasEquipment ? mdiCheck : mdiMinus" />
            </template>
            <template #[`item.schedule.appointmentStart`]="{ item }">
              <span
                v-if="
                  dateEquals(
                    item.schedule.occupancyStart,
                    item.schedule.occupancyEnd
                  )
                "
              >
                {{
                  t("common.format.dateSingle", {
                    date: useDateFormat(
                      item.schedule.occupancyStart,
                      DATE_FORMAT_DDMMYY
                    ).value,
                  })
                }}
              </span>

              <span v-else>
                {{
                  t("common.format.dateRange", {
                    start: useDateFormat(
                      item.schedule.occupancyStart,
                      DATE_FORMAT_DDMMYY
                    ).value,
                    end: useDateFormat(
                      item.schedule.occupancyEnd,
                      DATE_FORMAT_DDMMYY
                    ).value,
                  })
                }}
              </span>
            </template>
            <template #[`item.schedule.occupancyStart`]="{ item }">
              <span>
                {{
                  t("common.format.dateRange", {
                    start: useDateFormat(
                      item.schedule.occupancyStart,
                      TIME_FORMAT_HHMM
                    ).value,
                    end: useDateFormat(
                      item.schedule.occupancyEnd,
                      TIME_FORMAT_HHMM
                    ).value,
                  })
                }}
              </span>
            </template>
            <template #[`item.bookedBy`]="{ item }">
              <span>
                {{ item.bookedFor.firstName }} {{ item.bookedFor.lastName }}
              </span>
            </template>
            <template #[`item.hasNote`]="{ item }">
              <v-icon :icon="item.hasNote ? mdiCheck : mdiMinus" />
            </template>
            <template #[`item.actions`]="{ item }">
              <v-row align-content="center">
                <v-col
                  class="pa-0"
                  cols="12"
                  sm="6"
                >
                  <action-button
                    class="mr-1"
                    type="edit"
                    @click="
                      router.push({
                        name: isMyBooking
                          ? ROUTES.MY_BOOKINGS_EDIT
                          : ROUTES.BOOKINGS_EDIT,
                        params: { id: item.id },
                      })
                    "
                  />
                </v-col>
                <v-col
                  class="pa-0"
                  cols="12"
                  sm="6"
                >
                  <action-button :icon="mdiCalendarEditOutline" />
                </v-col>
              </v-row>
            </template>
          </v-data-table-server>
        </template>
      </v-card>
    </template>
  </base-view>
</template>

<script setup lang="ts">
import type { BookingListResponseDTO } from "@/api/raumreservierung-backend";
import type { SortItem } from "@/types/SortItem";
import type { TableHeader } from "@/types/TableHeader.ts";

import {
  mdiCalendarEditOutline,
  mdiCalendarEndOutline,
  mdiCalendarStartOutline,
  mdiCheck,
  mdiMinus,
} from "@mdi/js";
import { useDateFormat } from "@vueuse/core";
import { useRouteQuery } from "@vueuse/router";
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

import GeneralStatusSelect from "@/components/booking/GeneralStatusSelect.vue";
import StatusChip from "@/components/booking/StatusChip.vue";
import BaseView from "@/components/common/BaseView.vue";
import ActionButton from "@/components/common/buttons/ActionButton.vue";
import RoomSelect from "@/components/rooms/RoomSelect.vue";
import { useGetBookings } from "@/composables/api/useBookingsApi.ts";
import { useIsPrivileged } from "@/composables/useIsPrivileged.ts";
import { DATE_FORMAT_DDMMYY, TIME_FORMAT_HHMM } from "@/constants.ts";
import { ROUTES } from "@/types/Routes.ts";
import { dateEquals, toApiDate } from "@/util/timeUtil.ts";

const route = useRoute();
const router = useRouter();

const { t } = useI18n();

const isMyBooking = computed(() => route.name === ROUTES.MY_BOOKINGS_LIST);

const canEditBookings = useIsPrivileged("bookings:manage");

// ####### Page Filter and Options #########
const roomId = useRouteQuery("roomId", undefined);

const page = useRouteQuery("page", 1, { transform: Number });
const itemsPerPage = useRouteQuery("itemsPerPage", 10, { transform: Number });

const dateTransform = {
  get: (v: string | null) => (v ? new Date(v) : undefined),
  set: (v: Date | undefined): string | null => (v ? v.toISOString() : null),
};

const start = useRouteQuery("start", new Date().toISOString(), {
  transform: dateTransform,
});
const end = useRouteQuery("end", undefined, {
  transform: dateTransform,
});

const statusFilter = useRouteQuery("status", []);

const sortBy = useRouteQuery<string | undefined, SortItem[]>(
  "sort",
  undefined,
  {
    transform: {
      get: (v) => {
        if (!v) {
          return [];
        }

        const parts = v.split(",");
        const key = parts[0];
        const order = parts[1];

        return !key
          ? []
          : [
              {
                key,
                order: order === "desc" ? "desc" : "asc",
              },
            ];
      },
      set: (v) => {
        return !v[0] ? undefined : `${v[0].key},${v[0].order}`;
      },
    },
  }
);
// ####### Page Filter and Options #########

const {
  call: getBookings,
  data: bookingsPage,
  loading: getBookingsLoading,
} = useGetBookings();

const handleRowClick = (
  event: PointerEvent,
  { item }: { item: BookingListResponseDTO }
) => {
  router.push({
    name: isMyBooking.value
      ? ROUTES.MY_BOOKINGS_DETAILS
      : ROUTES.BOOKINGS_DETAILS,
    params: { id: item.id },
  });
};

const applyFilters = () => {
  // Changing the page will trigger a fetchPage sometimes
  page.value = 1;

  fetchPage();
};

const displayOptions = () => {
  fetchPage();
};

const fetchPage = async () => {
  const firstSort = sortBy.value[0];
  const sort = firstSort ? [`${firstSort.key},${firstSort.order}`] : [];

  await getBookings({
    page: (page.value ?? 1) - 1,
    size: itemsPerPage.value,
    sort,
    roomId: roomId.value,
    start: toApiDate(start.value),
    end: toApiDate(end.value),
    self: isMyBooking.value,
    status: statusFilter.value,
  });
};

const headers = computed(
  () =>
    [
      {
        title: "Status",
        value: "status",
        sortable: true,
      },
      { title: "Raumname", value: "room.name" },
      { title: "Teilnehmende", value: "participantCount", align: "center" },
      { title: "Veranstaltung", value: "title" },
      { title: "Datum", value: "schedule.appointmentStart", sortable: true },
      { title: "Uhrzeit", value: "schedule.occupancyStart", sortable: true },
      { title: "Gebucht für", value: "bookedBy", sortable: true },
      { title: "Ausstattung", value: "hasEquipment", align: "center" },
      { title: "Bemerkung", value: "hasNote", align: "center" },
      ...(canEditBookings
        ? [
            {
              title: t("common.action", { count: 2 }),
              value: "actions",
              align: "center",
            },
          ]
        : []),
    ] as TableHeader<BookingListResponseDTO>[]
);
</script>

<style scoped></style>
