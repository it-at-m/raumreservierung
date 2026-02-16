import type { GetTheEntityRequest } from "@/api/raumreservierung-backend";
import type User from "@/types/User.ts";

import { TheEntityControllerApi } from "@/api/raumreservierung-backend";
import { getUser } from "@/api/user-client.ts";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

export const useUserInfo = () => {
  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<void, User>(() => getUser());
};

export const useGetActuatorInfo = () => {
  const api = ApiFactory.getInstance(TheEntityControllerApi);

  return useApi<GetTheEntityRequest, object>((params) =>
    api.getTheEntity(params)
  );
};
