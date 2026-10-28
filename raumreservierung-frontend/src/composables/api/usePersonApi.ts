import type {
  CreatePersonRequest,
  DeletePersonRequest,
  FindById200Response,
  GetPersonsByPageableAndFilterRequest,
  PagedModelPersonResponseDto,
  UpdatePersonOperationRequest,
} from "@/api/raumreservierung-backend";
import type { Ref } from "vue";

import { useQuery } from "@tanstack/vue-query";
import { computed } from "vue";

import { PersonControllerApi } from "@/api/raumreservierung-backend";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";
import { CACHE_KEYS } from "@/types/CACHE_KEYS.ts";

export const useFindPerson = (personId: Ref<string | undefined>) => {
  const api = ApiFactory.getInstance(PersonControllerApi);

  return useQuery({
    queryKey: [CACHE_KEYS.PERSON, personId],
    queryFn: () => {
      if (!personId.value) {
        throw new Error("Person ID is required");
      }

      return api.findById({ personId: personId.value });
    },
    enabled: computed(() => !!personId.value),
  });
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
