import type {
  CreateEquipmentRequest,
  DeleteEquipmentRequest,
  UpdateEquipmentRequest,
} from "@/api/raumreservierung-backend";

import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";

import { EquipmentControllerApi } from "@/api/raumreservierung-backend";
import { ApiFactory } from "@/util/apiFactory.ts";

const EQUIPMENT_KEY = "allEquipment";

export const useGetAllEquipments = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);

  return useQuery({
    queryKey: [EQUIPMENT_KEY],
    queryFn: () => api.getAllEquipments(),
  });
};

export const useCreateEquipment = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: CreateEquipmentRequest) => api.createEquipment(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [EQUIPMENT_KEY] });
    },
  });
};

export const useUpdateEquipment = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: UpdateEquipmentRequest) => api.updateEquipment(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [EQUIPMENT_KEY] });
    },
  });
};

export const useDeleteEquipment = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: DeleteEquipmentRequest) => api.deleteEquipment(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [EQUIPMENT_KEY] });
    },
  });
};
