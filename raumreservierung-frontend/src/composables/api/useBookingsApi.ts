import type {
  BookingDetailResponseDTO,
  CreateBookingRequest,
  GetBookingRequest,
  GetBookingsByPageableAndFilterRequest,
  PagedModelBookingListResponseDTO,
  UpdateBookingRequest,
} from "@/api/raumreservierung-backend";

import { BookingControllerApi } from "@/api/raumreservierung-backend";
import { useApi } from "@/composables/api/useApi.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

export const useGetBooking = () => {
  const api = ApiFactory.getInstance(BookingControllerApi);

  return useApi<GetBookingRequest, BookingDetailResponseDTO>((params) =>
    api.getBooking(params)
  );
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
