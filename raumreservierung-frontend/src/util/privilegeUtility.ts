import type {
  Action,
  Privilege,
  PrivilegeAction,
  PrivilegeKey,
} from "@/types/Privilege.ts";
import type { Role } from "@/types/Role.ts";

import { PRIVILEGE_DIVIDER } from "@/types/Privilege.ts";

/**
 * Mapping table of roles and privileges
 */
const rolePrivilegeMapping = {
  raumadmin: [
    "rooms:write",
    "bookings:manage",
    "bookings:self",
    "bookings:read",
    "bookings:write",
    "calendar:write",
    "days:write",
    "equipment:write",
    "seating:write",
    "users:manage",
    "appointments:self",
  ],
  raumbuchung: [
    "bookings:self",
    "bookings:write",
    "bookings:read",
    "bookings:manage",
    "calendar:write",
    "rooms:read",
    "appointments:self",
  ],
  terminorganisator: [
    "bookings:self",
    "calendar:read",
    "rooms:read",
    "bookings:write",
    "bookings:read",
    "bookings:manage",
    "appointments:self",
  ],
  leseberechtigt: [
    "bookings:self",
    "calendar:read",
    "rooms:read",
    "appointments:self",
  ],
  anwender: ["bookings:self", "rooms:read", "appointments:self"],
} satisfies Record<Role, readonly Privilege[]>;

/**
 * Compares the two array and determines if the provided privileges are sufficient
 * @param givenPrivileges given privileges of an entity
 * @param requiredPrivileges necessary privileges for an action
 */
export const hasPrivileges = (
  givenPrivileges: Readonly<Privilege | Privilege[]>,
  requiredPrivileges: Readonly<Privilege | Privilege[]>
): boolean => {
  const requiredPrivilegesList = Array.isArray(requiredPrivileges)
    ? requiredPrivileges
    : [requiredPrivileges];

  return requiredPrivilegesList.some((r) => givenPrivileges.includes(r));
};

/**
 * Calculates the corresponding privileges
 */
export const mapSimpleRoleToPrivileges = (
  userRole: Role | undefined
): Privilege[] => {
  if (!userRole) {
    return [];
  }
  const privileges = rolePrivilegeMapping[userRole];

  return [...new Set(privileges)];
};

/**
 * Extracts the privilege key of a privilege
 */
export const parsePrivilegeKey = (privilege: Privilege) => {
  const [key] = privilege.split(PRIVILEGE_DIVIDER);
  return key as PrivilegeKey;
};

/**
 * Extracts the privilege action of a privilege
 */
export const parsePrivilegeAction = (privilege: Privilege) => {
  const [, action] = privilege.split(PRIVILEGE_DIVIDER);
  return action as PrivilegeAction;
};

/**
 * Weighted sequence of the privilege actions.
 */
const PRIVILEGE_ACTION_RANKING: Record<Action, number> = {
  manage: 30,
  write: 20,
  read: 10,
  self: -1,
};

/**
 * Normalizes an array of privileges by the weighted sequence of actions.
 * Higher actions overwrite lower ones.
 * Actions with negativ value are not going to be normalized.
 */
export const normalizePrivileges = (privileges: readonly Privilege[]) => {
  const exceptedPrivileges = new Set<Privilege>();
  const normalizedPrivileges = new Map<PrivilegeKey, Privilege>();

  privileges.forEach((privilege) => {
    if (PRIVILEGE_ACTION_RANKING[parsePrivilegeAction(privilege)] < 0) {
      exceptedPrivileges.add(privilege);
    } else {
      const key = parsePrivilegeKey(privilege);
      const action = parsePrivilegeAction(privilege);
      const found = normalizedPrivileges.get(key);

      if (
        !found ||
        PRIVILEGE_ACTION_RANKING[parsePrivilegeAction(found)] <
          PRIVILEGE_ACTION_RANKING[action]
      ) {
        normalizedPrivileges.set(key, privilege);
      }
    }
  });
  return [
    ...Array.from(exceptedPrivileges),
    ...Array.from(normalizedPrivileges.values()),
  ];
};
