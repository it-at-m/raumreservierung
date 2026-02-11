import type User from "@/types/User.ts";

import { getUser } from "@/api/user-client.ts";
import { useApi } from "@/composables/api/useApi.ts";

export const useUserInfo = () => {
  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<void, User>(() => getUser());
};
