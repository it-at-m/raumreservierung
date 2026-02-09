import type { Privilege } from "@/types/Privilege.ts";
import type { Role } from "@/types/Role.ts";

/**
 * Mapping table of roles and privileges
 */
const rolePrivilegeMapping = {
  fachadmin: ["rooms:write", "bookings:manage"],
  anwender: ["rooms:read", "bookings:self:read"],
} satisfies Record<Role, readonly Privilege[]>;

const roleKeys = Object.keys(rolePrivilegeMapping) as Role[];

/**
 * Compares the two array and determines if the provided privileges are sufficient
 * @param givenPrivileges given privileges of an entity
 * @param requiredPrivileges necessary privileges for an action
 */
export const hasPrivileges = (
  givenPrivileges: Readonly<Privilege | Privilege[]>,
  requiredPrivileges: Privilege | Privilege[]
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
