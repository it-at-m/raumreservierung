import type {
  BookingDetailResponseDTO,
  CreateBookingRequest,
  GetBookingRequest,
  GetBookingsByPageableAndFilterRequest,
  PagedModelBookingListResponseDTO,
  UpdateBookingRequest,
} from "@/api/raumreservierung-backend";
import type { MaybeRefOrGetter } from "vue";

import { useQuery } from "@tanstack/vue-query";
import { computed, toValue } from "vue";

import { BookingControllerApi } from "@/api/raumreservierung-backend";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

const BOOKING_KEY = "booking";

export const useGetBooking = () => {
  const api = ApiFactory.getInstance(BookingControllerApi);

  return useApi<GetBookingRequest, BookingDetailResponseDTO>((params) =>
    api.getBooking(params)
  );
};

export const useGetBookingTS = (
  params: MaybeRefOrGetter<string | undefined>
) => {
  const api = ApiFactory.getInstance(BookingControllerApi);
  const paramsRef = computed(() => toValue(params));

  return useQuery({
    queryKey: [BOOKING_KEY, paramsRef],
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

  return useApi<CreateBookingRequest, BookingDetailResponseDTO>((params) =>
    api.createBooking(params)
  );
};

export const useUpdateBooking = () => {
  const api = ApiFactory.getInstance(BookingControllerApi);

  return useApi<UpdateBookingRequest, BookingDetailResponseDTO>((params) =>
    api.updateBooking(params)
  );
};
