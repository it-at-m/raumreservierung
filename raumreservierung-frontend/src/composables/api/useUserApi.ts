import type User from "@/types/User.ts";

import { ActuatorApi } from "@/api/raumreservierung-backend";
import { getUser } from "@/api/user-client.ts";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

export const useUserInfo = () => {
  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<void, User>(() => getUser());
};

export const useGetActuatorInfo = () => {
  const api = ApiFactory.getInstance(ActuatorApi);

  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<void, object>(() => api.info());
};
