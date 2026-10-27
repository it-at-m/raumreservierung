import type {
  CreateHolidayRequest,
  DeleteHolidayRequest,
  GetHolidaysRequest,
  UpdateHolidayRequest,
} from "@/api/raumreservierung-backend/apis/HolidayControllerApi";
import type { MaybeRefOrGetter } from "vue";

import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import { computed, toValue } from "vue";

import { HolidayControllerApi } from "@/api/raumreservierung-backend/apis/HolidayControllerApi";
import { ApiFactory } from "@/util/apiFactory.ts";
import { CACHE_KEYS } from "@/types/CACHE_KEYS.ts";

export const useGetHolidays = (
  params: MaybeRefOrGetter<GetHolidaysRequest | undefined>
) => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  const paramsRef = computed(() => toValue(params));

  return useQuery({
    queryKey: [CACHE_KEYS.HOLIDAY, paramsRef],
    queryFn: () => {
      if (!paramsRef.value) {
        throw new Error("Holiday year is required");
      }
      return api.getHolidays(paramsRef.value);
    },
    enabled: computed(() => !!paramsRef.value),
  });
};

export const useDeleteHoliday = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: DeleteHolidayRequest) => api.deleteHoliday(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [CACHE_KEYS.HOLIDAY] });
    },
  });
};

export const useCreateHoliday = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: CreateHolidayRequest) => api.createHoliday(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [CACHE_KEYS.HOLIDAY] });
    },
  });
};

export const useUpdateHoliday = () => {
  const api = ApiFactory.getInstance(HolidayControllerApi);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: UpdateHolidayRequest) => api.updateHoliday(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [CACHE_KEYS.HOLIDAY] });
    },
  });
};
