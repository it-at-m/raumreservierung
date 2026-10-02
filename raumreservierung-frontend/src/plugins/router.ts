// Composables
import type { RouteRecordRaw } from "vue-router";

import { unref } from "vue";
import { createRouter, createWebHistory } from "vue-router";

import pinia from "@/plugins/pinia";
import { useUserStore } from "@/stores/user.ts";
import { ROUTES } from "@/types/Routes.ts";
import { hasPrivileges } from "@/util/privilegeUtility.ts";
import BookingsCalendarView from "@/views/bookings/BookingsCalendarView.vue";
import BookingsDetailsView from "@/views/bookings/BookingsDetailsView.vue";
import BookingsEditView from "@/views/bookings/BookingsEditView.vue";
import BookingsListView from "@/views/bookings/BookingsListView.vue";
import EquipmentView from "@/views/EquipmentView.vue";
import GetStartedView from "@/views/GetStartedView.vue";
import HelpView from "@/views/HelpView.vue";
import HolidaysView from "@/views/HolidaysView.vue";
import HomeView from "@/views/HomeView.vue";
import PersonView from "@/views/PersonView.vue";
import RoomsDetailsView from "@/views/rooms/RoomsDetailsView.vue";
import RoomsEditView from "@/views/rooms/RoomsEditView.vue";
import RoomsListView from "@/views/rooms/RoomsListView.vue";
import SeatingTypeView from "@/views/SeatingTypeView.vue";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: ROUTES.HOME,
    component: HomeView,
    meta: {},
  },
  {
    path: "/help",
    name: ROUTES.HELP,
    component: HelpView,
    meta: {},
  },
  {
    path: "/getstarted",
    name: ROUTES.GETSTARTED,
    component: GetStartedView,
    meta: {
      requiredPrivileges: ["rooms:write"],
    },
  },
  {
    path: "/admin/equipment",
    name: ROUTES.EQUIPMENT,
    component: EquipmentView,
    meta: {
      requiredPrivileges: ["equipment:write"],
    },
  },
  {
    path: "/admin/internalPerson",
    name: ROUTES.INTERNAL_PERSON,
    component: PersonView,
    meta: {
      requiredPrivileges: ["users:manage"],
    },
  },
  {
    path: "/admin/externalPerson",
    name: ROUTES.EXTERNAL_PERSON,
    component: PersonView,
    meta: {
      requiredPrivileges: ["users:manage"],
    },
  },
  {
    path: "/holidays/public",
    name: ROUTES.PUBLIC_HOLIDAYS,
    component: HolidaysView,
    meta: {
      requiredPrivileges: ["days:write"],
    },
  },
  {
    path: "/holidays/school",
    name: ROUTES.SCHOOL_HOLIDAYS,
    component: HolidaysView,
    meta: {
      requiredPrivileges: ["days:write"],
    },
  },
  // ROOMS
  {
    path: "/rooms",
    name: ROUTES.ROOMS_LIST,
    component: RoomsListView,
    meta: {
      requiredPrivileges: ["rooms:read", "rooms:write"],
    },
  },
  {
    path: "/rooms/new",
    name: ROUTES.ROOMS_CREATE,
    component: RoomsEditView,
    meta: {
      requiredPrivileges: ["rooms:write"],
    },
  },
  {
    path: "/rooms/:id",
    name: ROUTES.ROOMS_DETAILS,
    component: RoomsDetailsView,
    meta: {
      requiredPrivileges: ["rooms:read", "rooms:write"],
    },
  },
  {
    path: "/rooms/:id/edit",
    name: ROUTES.ROOMS_EDIT,
    component: RoomsEditView,
    meta: {
      requiredPrivileges: ["rooms:write"],
    },
  },
  // MY BOOKINGS
  {
    path: "/mybookings",
    name: ROUTES.MY_BOOKINGS_LIST,
    component: BookingsListView,
    meta: {
      requiredPrivileges: ["bookings:self"],
    },
  },
  {
    path: "/mybookings/new",
    name: ROUTES.MY_BOOKINGS_CREATE,
    component: BookingsEditView,
    meta: {
      requiredPrivileges: ["bookings:self"],
    },
  },
  {
    path: "/mybookings/:id",
    name: ROUTES.MY_BOOKINGS_DETAILS,
    component: BookingsDetailsView,
    meta: {
      requiredPrivileges: ["bookings:self"],
    },
  },
  {
    path: "/mybookings/:id/edit",
    name: ROUTES.MY_BOOKINGS_EDIT,
    component: BookingsEditView,
    meta: {
      requiredPrivileges: ["bookings:self"],
    },
  },
  // ALL BOOKINGS
  {
    path: "/bookings",
    name: ROUTES.BOOKINGS_LIST,
    component: BookingsListView,
    meta: {
      requiredPrivileges: ["bookings:read"],
    },
  },
  {
    path: "/bookings/new",
    name: ROUTES.BOOKINGS_CREATE,
    component: BookingsEditView,
    meta: {
      requiredPrivileges: ["bookings:write"],
    },
  },
  {
    path: "/bookings/:id",
    name: ROUTES.BOOKINGS_DETAILS,
    component: BookingsDetailsView,
    meta: {
      requiredPrivileges: ["bookings:read"],
    },
  },
  {
    path: "/bookings/:id/edit",
    name: ROUTES.BOOKINGS_EDIT,
    component: BookingsEditView,
    meta: {
      requiredPrivileges: ["bookings:manage"],
    },
  },
  {
    path: "/bookings/:id/calendar",
    name: ROUTES.BOOKINGS_CALENDAR,
    component: BookingsCalendarView,
    meta: {
      requiredPrivileges: ["bookings:manage"],
    },
  },
  {
    path: "/admin/seating",
    name: ROUTES.SEATING,
    component: SeatingTypeView,
    meta: {
      requiredPrivileges: ["seating:write"],
    },
  },
  { path: "/:catchAll(.*)*", redirect: "/" }, // CatchAll route
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return {
      top: 0,
      left: 0,
    };
  },
});

/**
 * Navigation guard checking privileges
 */
router.beforeEach(async (to) => {
  const userStore = useUserStore(pinia);
  if (!userStore.user) {
    await userStore.fetchUser();
  }

  if (!to.meta.requiredPrivileges) {
    return;
  }

  const convertedPrivileges = Array.isArray(to.meta.requiredPrivileges)
    ? to.meta.requiredPrivileges
    : [to.meta.requiredPrivileges];

  if (hasPrivileges(unref(userStore.privileges), convertedPrivileges)) {
    return;
  }

  return { name: ROUTES.HOME };
});

export default router;
