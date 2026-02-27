import type {
  CreateSeatingTypeRequest,
  DeleteSeatingTypeRequest,
  SeatingTypeResponseDto,
  UpdateSeatingTypeRequest,
} from "@/api/raumreservierung-backend";

import { SeatingControllerApi } from "@/api/raumreservierung-backend";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

export const useGetAllSeatingTypes = () => {
  const api = ApiFactory.getInstance(SeatingControllerApi);

  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<void, SeatingTypeResponseDto[]>(() => api.getAllSeatingTypes());
};
export const useCreateSeatingType = () => {
  const api = ApiFactory.getInstance(SeatingControllerApi);

  return useApi<CreateSeatingTypeRequest, SeatingTypeResponseDto>((params) =>
    api.createSeatingType(params)
  );
};
export const useUpdateSeatingType = () => {
  const api = ApiFactory.getInstance(SeatingControllerApi);

  return useApi<UpdateSeatingTypeRequest, SeatingTypeResponseDto>((params) =>
    api.updateSeatingType(params)
  );
};
export const useDeleteSeatingType = () => {
  const api = ApiFactory.getInstance(SeatingControllerApi);

  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<DeleteSeatingTypeRequest, void>((params) =>
    api.deleteSeatingType(params)
  );
};
