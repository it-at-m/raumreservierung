import type {
  BookingDetailResponseDTO,
  CreateBookingRequest,
  GetBookingRequest,
  GetBookingsByPageableAndFilterRequest,
  PagedModelBookingListResponseDTO,
  UpdateBookingRequest,
} from "@/api/raumreservierung-backend";
import type { MaybeRefOrGetter } from "vue";

import { useQuery, useQueryClient } from "@tanstack/vue-query";
import { computed, toValue } from "vue";

import { BookingControllerApi } from "@/api/raumreservierung-backend";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";
import { CACHE_KEYS } from "@/types/CACHE_KEYS.ts";

export const useGetBookingOld = () => {
  const api = ApiFactory.getInstance(BookingControllerApi);

  return useApi<GetBookingRequest, BookingDetailResponseDTO>((params) =>
    api.getBooking(params)
  );
};

export const useGetBooking = (params: MaybeRefOrGetter<string | undefined>) => {
  const api = ApiFactory.getInstance(BookingControllerApi);
  const paramsRef = computed(() => toValue(params));

  return useQuery({
    queryKey: [CACHE_KEYS.BOOKING, paramsRef],
    queryFn: () => {
      if (!paramsRef.value) {
        throw new Error("ID is required");
      }

      return api.getBooking({ bookingId: paramsRef.value });
    },
    enabled: computed(() => !!paramsRef.value),
  });
};

export const useGetBookings = () => {
  const api = ApiFactory.getInstance(BookingControllerApi);

  return useApi<
    GetBookingsByPageableAndFilterRequest,
    PagedModelBookingListResponseDTO
  >((params) => api.getBookingsByPageableAndFilter(params));
};

export const useCreateBooking = () => {
  const api = ApiFactory.getInstance(BookingControllerApi);
  const queryClient = useQueryClient();

  return useApi<CreateBookingRequest, BookingDetailResponseDTO>((params) =>
    api.createBooking(params).then((response) => {
      queryClient.invalidateQueries({
        queryKey: [CACHE_KEYS.BOOKING],
      });
      queryClient.invalidateQueries({
        queryKey: [CACHE_KEYS.APPOINTMENT],
      });
      return response;
    })
  );
};

export const useUpdateBooking = () => {
  const api = ApiFactory.getInstance(BookingControllerApi);
  const queryClient = useQueryClient();

  return useApi<UpdateBookingRequest, BookingDetailResponseDTO>((params) =>
    api.updateBooking(params).then((response) => {
      queryClient.invalidateQueries({
        queryKey: [CACHE_KEYS.BOOKING, params.bookingId],
      });
      queryClient.invalidateQueries({
        queryKey: [CACHE_KEYS.APPOINTMENT],
      });
      return response;
    })
  );
};
