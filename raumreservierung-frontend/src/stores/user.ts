import type { Privilege } from "@/types/Privilege.ts";

import { defineStore } from "pinia";
import { computed, readonly, ref } from "vue";

import User from "@/types/User";
import { mapSimpleRolesToPrivileges } from "@/util/privilegesCalculator.ts";

export interface UserState {
  user: User | null;
}

export const useUserStore = defineStore("user", () => {
  const user = ref<User | null>(null);
  const privileges = ref<Privilege[]>([]);

  const getUser = computed((): User | null => {
    return user.value;
  });

  const getPrivileges = () => readonly(privileges);

  function setUser(payload: User | null): void {
    privileges.value = mapSimpleRolesToPrivileges(payload?.user_roles || []);
    user.value = payload;
  }
  return { getUser, setUser, getPrivileges };
});
