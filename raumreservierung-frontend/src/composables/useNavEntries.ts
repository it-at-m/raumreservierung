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
    textKey: "Meine Buchungen einsehen",
    to: { name: ROUTES.HOME },
    requiredPrivilege: "bookings:self",
  },
  {
    kind: "group",
    id: "bookings:manage",
    textKey: "Buchen",
    requiredPrivilege: "bookings:manage",
    children: [
      {
        kind: "item",
        id: "roomBook",
        textKey: "über Räume buchen",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "rooms:read",
      },
      {
        kind: "item",
        id: "calendarBook",
        textKey: "über Kalendar buchen",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "calendar:write",
      },
    ],
  },
  {
    kind: "group",
    id: "persons",
    textKey: "Personen verwalten",
    requiredPrivilege: "users:write",
    children: [
      {
        kind: "item",
        id: "internalPerson",
        textKey: "Interne Personen verwalten",
        to: { name: ROUTES.GETSTARTED },
      },
      {
        kind: "item",
        id: "externalPerson",
        textKey: "Externe Personen verwalten",
        to: { name: ROUTES.GETSTARTED },
      },
    ],
  },
  {
    kind: "group",
    id: "dayManage",
    textKey: "Tage verwalten",
    requiredPrivilege: "days:write",
    children: [
      {
        kind: "item",
        id: "feiertag",
        textKey: "Feiertage verwalten",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "days:write",
      },
      {
        kind: "item",
        id: "Ferientage",
        textKey: "Ferien verwalten",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "days:write",
      },
    ],
  },
  {
    kind: "item",
    id: "roomsView",
    textKey: "Räume und Details ansehen",
    to: { name: ROUTES.GETSTARTED },
    requiredPrivilege: "rooms:read",
  },
  {
    kind: "item",
    id: "rooms",
    textKey: "Räume verwalten",
    to: { name: ROUTES.GETSTARTED },
    requiredPrivilege: "rooms:write",
  },
  {
    kind: "group",
    id: "equipment",
    textKey: "Ressourcen verwalten",
    requiredPrivilege: "equipment:write",
    children: [
      {
        kind: "item",
        id: "ausstattung",
        textKey: "Ausstattung verwalten",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "equipment:write",
      },
      {
        kind: "item",
        id: "bestuhlung",
        textKey: "Bestuhlung verwalten",
        to: { name: ROUTES.GETSTARTED },
        requiredPrivilege: "equipment:write",
      },
    ],
  },
  {
    kind: "item",
    id: "help",
    textKey: "Hilfe",
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
