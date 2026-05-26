<template>
  <base-view header-text="Buchungen verwalten">
    <template #default>
      <v-card titel="Anfragen, Reservierungen und Buchungen">
        <template #text>
          <v-row>
            <v-col>
              <v-select
                v-model="currentPageFilter.roomId"
                :loading="getRoomsLoading"
                :items="allRooms ?? []"
                item-title="name"
                item-value="id"
                clearable
                label="Raumfilter"
                variant="outlined"
                density="compact"
                @update:model-value="fetchPage"
              />
            </v-col>
            <v-col>
              <v-date-input
                v-model="currentPageFilter.start"
                density="compact"
                variant="outlined"
                clearable
                @update:model-value="fetchPage"
              />
            </v-col>
            <v-col>
              <v-date-input
                v-model="currentPageFilter.end"
                density="compact"
                variant="outlined"
                clearable
                @update:model-value="fetchPage"
              />
            </v-col>
          </v-row>
          <v-data-table-server
            :headers="headers"
            :items="bookingsPage?.content || []"
            :items-length="bookingsPage?.page?.totalElements || 0"
            :loading="getBookingsLoading"
            :sort-by="currentPageOptions.sortBy"
            @update:options="updateOptionsAndLoadPage"
            @click:row="handleRowClick"
          >
            <template #[`item.id`]> STATUS-LALAL </template>
            <template #[`item.hasEquipment`]="{ item }">
              <v-icon :icon="item.hasEquipment ? mdiCheck : mdiMinus" />
            </template>
            <template #[`item.schedule.appointmentStart`]="{ item }">
              <span>
                {{
                  useDateFormat(
                    item.schedule.occupancyStart,
                    DATE_FORMAT_DDMMYY
                  )
                }}
              </span>
              <span
                v-if="
                  !dateEquals(
                    item.schedule.occupancyStart,
                    item.schedule.occupancyEnd
                  )
                "
              >
                -
                {{
                  useDateFormat(item.schedule.occupancyEnd, DATE_FORMAT_DDMMYY)
                }}
              </span>
            </template>
            <template #[`item.schedule.occupancyStart`]="{ item }">
              <span>
                {{
                  useDateFormat(item.schedule.occupancyStart, TIME_FORMAT_HHMM)
                }}
                -
                {{
                  useDateFormat(item.schedule.occupancyEnd, TIME_FORMAT_HHMM)
                }}
              </span>
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
import type { TableHeader } from "@/types/TableHeader.ts";

import { mdiCalendarEditOutline, mdiCheck, mdiMinus } from "@mdi/js";
import { useDateFormat } from "@vueuse/core";
import { useRouteQuery } from "@vueuse/router";
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

import BaseView from "@/components/common/BaseView.vue";
import ActionButton from "@/components/common/buttons/ActionButton.vue";
import { useGetBookings } from "@/composables/api/useBookingsApi.ts";
import { useGetAllRooms } from "@/composables/api/useRoomsApi.ts";
import { useIsPrivileged } from "@/composables/useIsPrivileged.ts";
import { DATE_FORMAT_DDMMYY, TIME_FORMAT_HHMM } from "@/constants.ts";
import { ROUTES } from "@/types/Routes.ts";
import { dateEquals, toApiDate } from "@/util/timeUtil.ts";

const route = useRoute();
const router = useRouter();

const { t } = useI18n();

const canEditBookings = useIsPrivileged("bookings:manage");

const isMyBooking = computed(() => route.name === ROUTES.MY_BOOKINGS_LIST);

interface SortItem {
  key: string;
  order: "asc" | "desc";
}
interface PageFilter {
  start?: Date;
  end?: Date;
  roomId?: string;
}

interface PageOptions {
  page: number;
  itemsPerPage: number;
  sortBy: SortItem[];
}

const currentPageOptions = ref<PageOptions>({
  page: 0,
  itemsPerPage: 10,
  sortBy: [],
});

const currentPageFilter = ref<PageFilter>({});

// ####### Page Filter and Options #########
const roomId = useRouteQuery("roomId", undefined);

const page = useRouteQuery("page", 1, { transform: Number });
const itemsPerPage = useRouteQuery("itemsPerPage", 10, { transform: Number });

const dateTransform = {
  get: (v: string | null) => (v ? new Date(v) : undefined),
  set: (v: Date | undefined) => (v ? toApiDate(v) : undefined), // toApiDate formatiert es für die URL/API
};

const start = useRouteQuery("start", undefined, { transform: dateTransform });
const end = useRouteQuery("end", undefined, { transform: dateTransform });

const sortBy = useRouteQuery<SortItem[], string>("sort", [], {
  transform: {
    // URL-String ("id,asc") zurück in ein Array of Objects für Vuetify verwandeln
    get: (v) => {
      if (!v) return [];
      const [key, order] = v.split(",");
      return [{ key, order: order as "asc" | "desc" }];
    },
    // Vuetify Sortier-Array in einen flachen String für die URL verwandeln
    set: (v) => {
      if (!v || v.length === 0) return undefined;
      return `${v[0].key},${v[0].order}`;
    },
  },
});

// ####### Page Filter and Options #########

const {
  call: getBookings,
  data: bookingsPage,
  loading: getBookingsLoading,
} = useGetBookings();

const {
  call: getRooms,
  data: allRooms,
  loading: getRoomsLoading,
} = useGetAllRooms();

onMounted(async () => {
  await fetchPage();

  await getRooms();
});

const updateOptionsAndLoadPage = async (options: PageOptions) => {
  currentPageOptions.value = options;

  await fetchPage();
};

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

const fetchPage = async () => {
  const sort =
    currentPageOptions.value.sortBy.length > 0
      ? currentPageOptions.value.sortBy.map(
          (item) => `${item.key},${item.order}`
        )
      : [];

  await getBookings({
    page: currentPageOptions.value.page - 1,
    size: currentPageOptions.value.itemsPerPage,
    sort,
    ...currentPageFilter.value,
    start: toApiDate(currentPageFilter.value.start),
    end: toApiDate(currentPageFilter.value.start),
    self: isMyBooking.value,
  });
};

const headers = computed(
  () =>
    [
      {
        title: "Status",
        value: "id",
        sortable: true,
      },
      { title: "Raumname", value: "room.name" },
      { title: "Teilnehmende", value: "participantCount", align: "center" },
      { title: "Veranstaltung", value: "title" },
      { title: "Datum", value: "schedule.appointmentStart", sortable: true },
      { title: "Uhrzeit", value: "schedule.occupancyStart", sortable: true },
      { title: "Ausstattung", value: "hasEquipment", align: "center" },
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
