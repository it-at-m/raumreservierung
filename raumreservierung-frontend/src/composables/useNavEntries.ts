import type { NavEntry } from "@/types/NavigationEntries.ts";
import type { Privilege } from "@/types/Privilege.ts";

import { computed, unref } from "vue";

import { useUserStore } from "@/stores/user.ts";
import { ROUTES } from "@/types/Routes.ts";
import { normalizePrivileges } from "@/util/privilegeUtility.ts";

export const useNavEntries = () => {
  const userStore = useUserStore();

  return computed(() =>
    calculateNavEntries(NAV_ENTRIES, unref(userStore.getPrivileges()))
  );
};

const NAV_ENTRIES: readonly NavEntry[] = [
  {
    kind: "item",
    id: "bookings:self",
    textKey: "navigationDrawer.myBookings",
    to: { name: ROUTES.HOME },
    requiredPrivilege: "bookings:self",
  },
  {
    kind: "group",
    id: "bookings:manage",
    textKey: "navigationDrawer.bookingGroup.groupTitle",
    requiredPrivilege: "bookings:manage",
    children: [
      {
        kind: "item",
        id: "roomBook",
        textKey: "navigationDrawer.bookingGroup.bookRooms",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "rooms:read",
      },
      {
        kind: "item",
        id: "calendarBook",
        textKey: "navigationDrawer.bookingGroup.bookCalendar",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "calendar:write",
      },
    ],
  },
  {
    kind: "group",
    id: "persons",
    textKey: "navigationDrawer.personGroup.groupTitle",
    requiredPrivilege: "users:write",
    children: [
      {
        kind: "item",
        id: "internalPerson",
        textKey: "navigationDrawer.personGroup.internalPersons",
        to: { name: ROUTES.GETSTARTED },
      },
      {
        kind: "item",
        id: "externalPerson",
        textKey: "navigationDrawer.personGroup.externalPersons",
        to: { name: ROUTES.GETSTARTED },
      },
    ],
  },
  {
    kind: "group",
    id: "dayManage",
    textKey: "navigationDrawer.holidayGroup.groupTitle",
    requiredPrivilege: "days:write",
    children: [
      {
        kind: "item",
        id: "feiertag",
        textKey: "navigationDrawer.holidayGroup.publicHolidays",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "days:write",
      },
      {
        kind: "item",
        id: "Ferientage",
        textKey: "navigationDrawer.holidayGroup.schoolHolidays",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "days:write",
      },
    ],
  },
  {
    kind: "item",
    id: "roomsView",
    textKey: "navigationDrawer.viewRoomsAndDetails",
    to: { name: ROUTES.GETSTARTED },
    requiredPrivilege: "rooms:read",
  },
  {
    kind: "item",
    id: "rooms",
    textKey: "navigationDrawer.manageRooms",
    to: { name: ROUTES.GETSTARTED },
    requiredPrivilege: "rooms:write",
  },
  {
    kind: "group",
    id: "equipment",
    textKey: "navigationDrawer.equipmentGroup.groupTitle",
    requiredPrivilege: "equipment:write",
    children: [
      {
        kind: "item",
        id: "ausstattung",
        textKey: "navigationDrawer.equipmentGroup.equipment",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "equipment:write",
      },
      {
        kind: "item",
        id: "bestuhlung",
        textKey: "navigationDrawer.equipmentGroup.seating",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "equipment:write",
      },
    ],
  },
  {
    kind: "item",
    id: "help",
    textKey: "navigationDrawer.help",
    to: { name: ROUTES.HELP },
  },
] as const;

const calculateNavEntries = (
  entries: readonly NavEntry[],
  userPrivileges: readonly Privilege[]
): NavEntry[] => {
  const normalizedUserPrivileges = normalizePrivileges(userPrivileges);

  return entries.filter((entry) => {
    return (
      !entry.requiredPrivilege ||
      normalizedUserPrivileges.some(
        (userPriv) => userPriv === entry.requiredPrivilege
      )
    );
  });
};
