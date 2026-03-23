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
  fachadmin: [
    "rooms:write",
    "bookings:manage",
    "bookings:self",
    "bookings:write",
    "calendar:write",
    "days:write",
    "equipment:write",
    "seating:write",
    "users:manage",
  ],
  anwender: ["bookings:self", "rooms:read"],
} satisfies Record<Role, readonly Privilege[]>;

const roleKeys = Object.keys(rolePrivilegeMapping) as Role[];

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

  return requiredPrivilegesList.every((r) => givenPrivileges.includes(r));
};

/**
 * Check if string matches one of the possible roles
 */
const isRole = (value: string): value is Role =>
  (roleKeys as readonly string[]).includes(value);

/**
 * Maps string array to roles and calculates the corresponding privileges
 */
export const mapSimpleRolesToPrivileges = (
  userRoles: string[]
): Privilege[] => {
  const privileges = userRoles
    .map((value) => value.toLowerCase())
    .filter(isRole)
    .flatMap((role) => rolePrivilegeMapping[role]);

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
