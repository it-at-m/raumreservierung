import type {
  BookingDetailResponseDTO,
  BookingRequestDTO,
  ScheduleTemplate,
} from "@/api/raumreservierung-backend";

import { SCHEDULE_DEFAULT_DURATION } from "@/constants.ts";

export const mapBookingResponseToRequest = (
  response: BookingDetailResponseDTO
): BookingRequestDTO => {
  const mappedEquipmentIds =
    response.equipments
      ?.map((equip) => equip.id)
      .filter((id): id is string => id !== undefined) || [];

  return {
    title: response.title,
    participantCount: response.participantCount,
    equipmentIds: mappedEquipmentIds,
    cateringNeeded: response.cateringNeeded,
    internalNotes: response.internalNotes,
    additionalNotes: response.additionalNotes,
    seatingTypeId: response.seatingType?.id,
    roomId: response.room?.id,
    bookedForId: response.bookedFor?.id,
    schedule: response.schedule,
    recurringRule: response.recurringRule,
    status: response.status.currentStatus,
    reasonForStatusChange: response.reasonForStatusChange,
  };
};

export const EMPTY_BOOKING_REQUEST_DATA: BookingRequestDTO = {
  title: "",
  participantCount: 1,
  equipmentIds: [],
  cateringNeeded: false,
  internalNotes: "",
  additionalNotes: "",
  seatingTypeId: "",
  recurringRule: undefined,
  roomId: undefined,
  bookedForId: undefined,
  schedule: {
    occupancyStart: new Date(),
    occupancyEnd: new Date(Date.now() + SCHEDULE_DEFAULT_DURATION),
    appointmentStart: undefined,
    appointmentEnd: undefined,
  } as ScheduleTemplate,
  status: "NEW",
  reasonForStatusChange: undefined,
};
