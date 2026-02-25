import type {
  CreateHolidayRequest,
  DeleteHolidayRequest,
  GetHolidaysRequest,
  UpdateHolidayRequest,
} from "@/api/raumreservierung-backend/apis/HolidayControllerApi";
import type { HolidayResponseDTO } from "@/api/raumreservierung-backend/models/HolidayResponseDTO.ts";

import { HolidayControllerApi } from "@/api/raumreservierung-backend/apis/HolidayControllerApi";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

export const useGetPublicHolidays = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  return useApi<GetHolidaysRequest, HolidayResponseDTO[]>(
    (params: GetHolidaysRequest) => api.getHolidays(params)
  );
};

export const useGetSchoolHolidays = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  return useApi<GetHolidaysRequest, HolidayResponseDTO[]>(
    (params: GetHolidaysRequest) => api.getHolidays(params)
  );
};

export const useDeleteHoliday = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<DeleteHolidayRequest, void>((params: DeleteHolidayRequest) =>
    api.deleteHoliday(params)
  );
};

export const useAddHoliday = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  return useApi<CreateHolidayRequest, HolidayResponseDTO>(
    (params: CreateHolidayRequest) => api.createHoliday(params)
  );
};

export const useEditHoliday = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  return useApi<UpdateHolidayRequest, HolidayResponseDTO>(
    (params: UpdateHolidayRequest) => api.updateHoliday(params)
  );
};
