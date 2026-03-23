import type { NavEntry } from "@/types/NavigationEntries.ts";
import type { Privilege } from "@/types/Privilege.ts";

import { computed, unref } from "vue";

import { useUserStore } from "@/stores/user.ts";
import { ROUTES } from "@/types/Routes.ts";
import { normalizePrivileges } from "@/util/privilegeUtility.ts";

export const useNavEntries = () => {
  const userStore = useUserStore();

  return computed(() =>
    calculateNavEntries(NAV_ENTRIES, unref(userStore.privileges))
  );
};

const NAV_ENTRIES: readonly NavEntry[] = [
  {
    kind: "item",
    textKey: "navigationDrawer.myBookings",
    to: { name: ROUTES.HOME },
    requiredPrivilege: "bookings:self",
  },
  {
    kind: "group",
    textKey: "navigationDrawer.bookingGroup.groupTitle",
    requiredPrivilege: "bookings:manage",
    children: [
      {
        kind: "item",
        textKey: "navigationDrawer.bookingGroup.bookRooms",
        to: { name: ROUTES.GETSTARTED },
      },
      {
        kind: "item",
        textKey: "navigationDrawer.bookingGroup.bookCalendar",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "calendar:write",
      },
    ],
  },
  {
    kind: "group",
    textKey: "navigationDrawer.personGroup.groupTitle",
    requiredPrivilege: "users:manage",
    children: [
      {
        kind: "item",
        textKey: "navigationDrawer.personGroup.internalPersons",
        to: { name: ROUTES.INTERNAL_PERSON },
      },
      {
        kind: "item",
        textKey: "navigationDrawer.personGroup.externalPersons",
        to: { name: ROUTES.EXTERNAL_PERSON },
      },
    ],
  },
  {
    kind: "group",
    textKey: "navigationDrawer.holidayGroup.groupTitle",
    requiredPrivilege: "days:write",
    children: [
      {
        kind: "item",
        textKey: "navigationDrawer.holidayGroup.publicHolidays",
        to: { name: ROUTES.PUBLIC_HOLIDAYS },
      },
      {
        kind: "item",
        textKey: "navigationDrawer.holidayGroup.schoolHolidays",
        to: { name: ROUTES.SCHOOL_HOLIDAYS },
      },
    ],
  },
  {
    kind: "item",
    textKey: "navigationDrawer.viewRoomsAndDetails",
    to: { name: ROUTES.GETSTARTED },
    requiredPrivilege: "rooms:read",
  },
  {
    kind: "item",
    textKey: "navigationDrawer.manageRooms",
    to: { name: ROUTES.GETSTARTED },
    requiredPrivilege: "rooms:write",
  },
  {
    kind: "group",
    textKey: "navigationDrawer.equipmentGroup.groupTitle",
    requiredPrivilege: "equipment:write",
    children: [
      {
        kind: "item",
        textKey: "navigationDrawer.equipmentGroup.equipment",
        to: { name: ROUTES.EQUIPMENT },
      },
      {
        kind: "item",
        textKey: "navigationDrawer.equipmentGroup.seating",
        to: { name: ROUTES.SEATING },
      },
    ],
  },
  {
    kind: "item",
    textKey: "navigationDrawer.help",
    to: { name: ROUTES.HELP },
  },
] as const;

const calculateNavEntries = (
  entries: readonly NavEntry[],
  userPrivileges: readonly Privilege[]
): NavEntry[] => {
  const normalizedUserPrivileges = new Set(normalizePrivileges(userPrivileges));

  const hasPrivilege = (required?: Privilege) =>
    !required || normalizedUserPrivileges.has(required);

  return entries
    .filter((entry) => hasPrivilege(entry.requiredPrivilege))
    .map((entry) =>
      entry.kind === "item"
        ? entry
        : {
            ...entry,
            children: entry.children.filter((child) =>
              hasPrivilege(child.requiredPrivilege)
            ),
          }
    )
    .filter((entry) => entry.kind === "item" || entry.children.length > 0);
};
