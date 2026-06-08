import { defineStore } from "pinia";
import { computed } from "vue";

import { Levels } from "@/api/error.ts";
import { useUserInfo } from "@/composables/api/useUserApi.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import User from "@/types/User";
import { mapSimpleRolesToPrivileges } from "@/util/privilegeUtility.ts";

export interface UserState {
  user: User | null;
}

export const useUserStore = defineStore("user", () => {
  const { call, data, loading, error } = useUserInfo();
  const snackbarStore = useSnackbarStore();

  const user = computed(() => data.value as User);
  const privileges = computed(
    () => mapSimpleRolesToPrivileges((data.value as User)?.user_roles || []) // internally mapped inside the getuserClient!
  );

  const fetchUser = async () => {
    if (loading.value) {
      return;
    }

    await call();
    if (error.value) {
      snackbarStore.add({
        level: Levels.WARNING,
        message: "Nutzer konnte nicht geladen werden.",
      });
    }
  };

  return { user, privileges, fetchUser };
});
