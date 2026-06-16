import type {
  CreateEquipmentRequest,
  DeleteEquipmentRequest,
  EquipmentResponseDto,
  UpdateEquipmentRequest,
} from "@/api/raumreservierung-backend";

import { useMutation, useQuery } from "@tanstack/vue-query";

import { EquipmentControllerApi } from "@/api/raumreservierung-backend";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

export const useGetAllEquipments = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);

  return useQuery({
    queryKey: ["allEquipment"],
    queryFn: () => api.getAllEquipments(),
  });
};

export const useCreateEquipment = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);

  return useMutation({
    mutationFn: (params: CreateEquipmentRequest) => api.createEquipment(params),
    onSuccess: () => {
      qu;
    },
  });
};

export const useCreateEquipmentOld = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);

  return useApi<CreateEquipmentRequest, EquipmentResponseDto>((params) =>
    api.createEquipment(params)
  );
};

export const useUpdateEquipment = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);

  return useApi<UpdateEquipmentRequest, EquipmentResponseDto>((params) =>
    api.updateEquipment(params)
  );
};

export const useDeleteEquipment = () => {
  const api = ApiFactory.getInstance(EquipmentControllerApi);

  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<DeleteEquipmentRequest, void>((params) =>
    api.deleteEquipment(params)
  );
};
