import type {
  AppointmentResponseDTO,
  UpdateAppointmentRequest,
} from "@/api/raumreservierung-backend";

import { AppointmentControllerApi } from "@/api/raumreservierung-backend";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

export const useUpdateAppointment = () => {
  const api = ApiFactory.getInstance(AppointmentControllerApi);

  return useApi<UpdateAppointmentRequest, AppointmentResponseDTO>((params) =>
    api.updateAppointment(params)
  );
};
