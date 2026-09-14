import type {
  CreateSeatingTypeRequest,
  DeleteSeatingTypeRequest,
  UpdateSeatingTypeRequest,
} from "@/api/raumreservierung-backend";
import type { Ref } from "vue";

import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import { computed } from "vue";

import { SeatingControllerApi } from "@/api/raumreservierung-backend";
import { ROOM_KEY } from "@/composables/api/useRoomsApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

const SEATING_TYPE_KEY = "allSeatingTypes";

export const useGetAllSeatingTypes = () => {
  const api = ApiFactory.getInstance(SeatingControllerApi);

  return useQuery({
    queryKey: [SEATING_TYPE_KEY],
    queryFn: () => api.getAllSeatingTypes(),
  });
};
export const useCreateSeatingType = () => {
  const api = ApiFactory.getInstance(SeatingControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: CreateSeatingTypeRequest) =>
      api.createSeatingType(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [SEATING_TYPE_KEY] });
    },
  });
};
export const useUpdateSeatingType = () => {
  const api = ApiFactory.getInstance(SeatingControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: UpdateSeatingTypeRequest) =>
      api.updateSeatingType(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [SEATING_TYPE_KEY] });
      queryClient.invalidateQueries({ queryKey: [ROOM_KEY] });
    },
  });
};
export const useDeleteSeatingType = () => {
  const api = ApiFactory.getInstance(SeatingControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: DeleteSeatingTypeRequest) =>
      api.deleteSeatingType(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [SEATING_TYPE_KEY] });
      queryClient.invalidateQueries({ queryKey: [ROOM_KEY] });
    },
  });
};

export const useCheckSeatingTypeDeletable = (
  seatingTypeId: Ref<string | undefined>
) => {
  const api = ApiFactory.getInstance(SeatingControllerApi);
  return useQuery({
    queryKey: ["seatingType", "deletable", seatingTypeId],
    queryFn: () => {
      if (!seatingTypeId.value) {
        throw new Error("Seating type ID is required");
      }
      return api.isSeatingTypeDeletable({
        seatingTypeId: seatingTypeId.value,
      });
    },
    enabled: computed(() => !!seatingTypeId.value),
  });
};
