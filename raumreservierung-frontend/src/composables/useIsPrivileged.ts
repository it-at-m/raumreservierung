import type { Privilege } from "@/types/RouterMetaField.ts";

import { computed } from "vue";

import { useUserStore } from "@/stores/user.ts";
import { hasPrivileges } from "@/util/privilegesCalculator.ts";

export const useIsPrivileged = (
  requiredPrivileges?: Privilege | Privilege[]
) => {
  const userStore = useUserStore();

  const check = (reqPriv: Privilege | Privilege[]) =>
    hasPrivileges(userStore.privileges, reqPriv);

  if (requiredPrivileges) {
    return computed(() => check(requiredPrivileges));
  }

  return check;
};
