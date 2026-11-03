import type {
  CreateEquipmentRequest,
  DeleteEquipmentRequest,
  UpdateEquipmentRequest,
} from "@/api/raumreservierung-backend";

import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";

import { EquipmentControllerApi } from "@/api/raumreservierung-backend";
import { ApiFactory } from "@/util/apiFactory.ts";
import { CACHE_KEYS } from "@/types/CACHE_KEYS.ts";

export const useGetAllEquipments = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);

  return useQuery({
    queryKey: [CACHE_KEYS.EQUIPMENT],
    queryFn: () => api.getAllEquipments(),
  });
};

export const useCreateEquipment = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: CreateEquipmentRequest) => api.createEquipment(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [CACHE_KEYS.EQUIPMENT] });
    },
  });
};

export const useUpdateEquipment = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: UpdateEquipmentRequest) => api.updateEquipment(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [CACHE_KEYS.EQUIPMENT] });
    },
  });
};

export const useDeleteEquipment = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: DeleteEquipmentRequest) => api.deleteEquipment(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [CACHE_KEYS.EQUIPMENT] });
    },
  });
};
