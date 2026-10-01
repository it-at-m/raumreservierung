import type {
  AppointmentResponseDTO,
  GetAppointmentsByPageableAndFilterRequest,
  PagedModelAppointmentDetailsResponseDTO,
  UpdateAppointmentRequest,
} from "@/api/raumreservierung-backend";
import type { MaybeRefOrGetter } from "vue";

import { keepPreviousData, useQuery } from "@tanstack/vue-query";
import { computed, toValue } from "vue";

import { AppointmentControllerApi } from "@/api/raumreservierung-backend";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

const APPOINTMENTS_KEY = "appointment";

export const useUpdateAppointment = () => {
  const api = ApiFactory.getInstance(AppointmentControllerApi);

  return useApi<UpdateAppointmentRequest, AppointmentResponseDTO>((params) =>
    api.updateAppointment(params)
  );
};

export const useGetAppointmentsOld = () => {
  const api = ApiFactory.getInstance(AppointmentControllerApi);

  return useApi<
    GetAppointmentsByPageableAndFilterRequest,
    PagedModelAppointmentDetailsResponseDTO
  >((params) => api.getAppointmentsByPageableAndFilter(params));
};

export const useGetAppointments = (
  params: MaybeRefOrGetter<
    GetAppointmentsByPageableAndFilterRequest | undefined
  >
) => {
  const api = ApiFactory.getInstance(AppointmentControllerApi);
  const paramsRef = computed(() => toValue(params));

  return useQuery({
    queryKey: [APPOINTMENTS_KEY, paramsRef],
    queryFn: async () => {
      const currentParams = paramsRef.value;

      if (!currentParams?.startDate || !currentParams?.endDate) {
        throw new Error("startDate and endDate are required");
      }

      return await api.getAppointmentsByPageableAndFilter(currentParams);
    },
    enabled: computed(
      () => !!paramsRef.value?.startDate && !!paramsRef.value?.endDate
    ),
    placeholderData: keepPreviousData,
  });
};
