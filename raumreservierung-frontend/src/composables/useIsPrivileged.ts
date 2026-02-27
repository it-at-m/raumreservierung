import type { Privilege } from "@/types/Privilege.ts";
import type { ComputedRef } from "vue";

import { computed, unref } from "vue";

import { useUserStore } from "@/stores/user.ts";
import { hasPrivileges } from "@/util/privilegeUtility.ts";

/**
 * Calculates a computed boolean if user is privileged
 */
export function useIsPrivileged(
  requiredPrivileges: Privilege | Privilege[]
): ComputedRef<boolean>;

/**
 * Returns executable function which returns if user is privileged
 */
export function useIsPrivileged(): (
  reqPriv: Privilege | Privilege[]
) => boolean;
export function useIsPrivileged(requiredPrivileges?: Privilege | Privilege[]) {
  const userStore = useUserStore();

  const check = (reqPriv: Privilege | Privilege[]) =>
    hasPrivileges(unref(userStore.privileges), reqPriv);

  if (requiredPrivileges !== undefined) {
    return computed(() => check(requiredPrivileges));
  }

  return check;
}
