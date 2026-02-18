import type { HolidayResponseDTO } from "@/api/raumreservierung-backend/models/HolidayResponseDTO.ts";

import { HolidayControllerApi } from "@/api/raumreservierung-backend/apis/HolidayControllerApi";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

export const useGetPublicHolidays = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<void, HolidayResponseDTO[]>(() => api.getPublicHolidays());
};

export const useGetSchoolHolidays = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<void, HolidayResponseDTO[]>(() =>
    api.getPublicHolidays()
  );
};


