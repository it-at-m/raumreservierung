import type {
  CreateSeatingTypeRequest,
  DeleteSeatingTypeRequest,
  IsRoomCapacityRemovableRequest,
  UpdateSeatingTypeRequest,
} from "@/api/raumreservierung-backend";

import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";

import { SeatingControllerApi } from "@/api/raumreservierung-backend";
import { ApiFactory } from "@/util/apiFactory.ts";
import { CACHE_KEYS } from "@/types/CACHE_KEYS.ts";

export const useGetAllSeatingTypes = () => {
  const api = ApiFactory.getInstance(SeatingControllerApi);

  return useQuery({
    queryKey: [CACHE_KEYS.SEATING_TYPE],
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
      queryClient.invalidateQueries({ queryKey: [CACHE_KEYS.SEATING_TYPE] });
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
      queryClient.invalidateQueries({ queryKey: [CACHE_KEYS.SEATING_TYPE] });
      queryClient.invalidateQueries({ queryKey: [CACHE_KEYS.ROOM] });
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
      queryClient.invalidateQueries({ queryKey: [CACHE_KEYS.SEATING_TYPE] });
      queryClient.invalidateQueries({ queryKey: [CACHE_KEYS.ROOM] });
    },
  });
};

export const useCheckSeatingTypeDeletableMutation = () => {
  const api = ApiFactory.getInstance(SeatingControllerApi);
  return useMutation({
    mutationFn: (seatingTypeId: string) =>
      api.isSeatingTypeDeletable({ seatingTypeId }),
  });
};

export const useCheckSeatingTypeDeletableInRoom = () => {
  const api = ApiFactory.getInstance(SeatingControllerApi);
  return useMutation({
    mutationFn: (params: IsRoomCapacityRemovableRequest) =>
      api.isRoomCapacityRemovable(params),
  });
};
