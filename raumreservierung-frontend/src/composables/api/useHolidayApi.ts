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

export const useGetHolidays = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  return useApi<GetHolidaysRequest, HolidayResponseDTO[]>((params) =>
    api.getHolidays(params)
  );
};

export const useDeleteHoliday = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
  return useApi<DeleteHolidayRequest, void>((params) =>
    api.deleteHoliday(params)
  );
};

export const useAddHoliday = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  return useApi<CreateHolidayRequest, HolidayResponseDTO>((params) =>
    api.createHoliday(params)
  );
};

export const useEditHoliday = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  return useApi<UpdateHolidayRequest, HolidayResponseDTO>((params) =>
    api.updateHoliday(params)
  );
};
