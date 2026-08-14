import { BookingRequestDTOBookingTypeEnum } from "@/api/raumreservierung-backend";

export function useBookingType() {
  const bookingTypeOptions = [
    {
      key: "domain.booking.types.default",
      value: BookingRequestDTOBookingTypeEnum.DEFAULT,
    },
    {
      key: "domain.booking.types.free",
      value: BookingRequestDTOBookingTypeEnum.FREE,
    },
    {
      key: "domain.booking.types.service",
      value: BookingRequestDTOBookingTypeEnum.SERVICE,
    },
  ];

  return {
    bookingTypeOptions,
  };
}
