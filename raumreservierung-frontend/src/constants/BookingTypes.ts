import { BookingRequestDTOBookingTypeEnum } from "@/api/raumreservierung-backend";

export const bookingTypeOptions = [
  {
    title: "domain.booking.types.default",
    value: BookingRequestDTOBookingTypeEnum.DEFAULT,
  },
  {
    title: "domain.booking.types.free",
    value: BookingRequestDTOBookingTypeEnum.FREE,
  },
  {
    title: "domain.booking.types.service",
    value: BookingRequestDTOBookingTypeEnum.SERVICE,
  },
];
