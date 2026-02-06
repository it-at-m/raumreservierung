import type { Privilege } from "@/types/RouterMetaField.ts";

export const hasPrivileges = (
  userPrivileges: string[],
  requiredPrivileges: Privilege | Privilege[]
) => {
  const requiredList = Array.isArray(requiredPrivileges)
    ? requiredPrivileges
    : [requiredPrivileges];

  return requiredList.every((r) => userPrivileges.includes(r));
};

export const mapRolesToPrivileges = (userRole: string): Privilege[] => {
  return ["rooms:read", "rooms:write"];
};
