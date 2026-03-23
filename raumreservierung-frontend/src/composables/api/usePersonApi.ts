import type {
  CreatePersonRequest,
  DeletePersonRequest,
  FindById200Response,
  FindByIdRequest,
  GetPersonsByPageableAndFilterRequest,
  PagedModelPersonResponseDto,
  UpdatePersonOperationRequest,
} from "@/api/raumreservierung-backend";

import { PersonControllerApi } from "@/api/raumreservierung-backend";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

export const useFindPerson = () => {
  const api = ApiFactory.getInstance(PersonControllerApi);

  return useApi<FindByIdRequest, FindById200Response>((params) =>
    api.findById(params)
  );
};

export const useGetPersonPage = () => {
  const api = ApiFactory.getInstance(PersonControllerApi);

  return useApi<
    GetPersonsByPageableAndFilterRequest,
    PagedModelPersonResponseDto
  >((params) => api.getPersonsByPageableAndFilter(params));
};

export const useCreatePerson = () => {
  const api = ApiFactory.getInstance(PersonControllerApi);

  return useApi<CreatePersonRequest, FindById200Response>((params) =>
    api.createPerson(params)
  );
};

export const useUpdatePerson = () => {
  const api = ApiFactory.getInstance(PersonControllerApi);

  return useApi<UpdatePersonOperationRequest, FindById200Response>((params) =>
    api.updatePerson(params)
  );
};

export const useDeletePerson = () => {
  const api = ApiFactory.getInstance(PersonControllerApi);

  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<DeletePersonRequest, void>((params) =>
    api.deletePerson(params)
  );
};
