import type {
  BookingStatusDTOCurrentStatusEnum as BookingStatus,
  BookingStatusDTO,
} from "@/api/raumreservierung-backend";
import type { ChipConfig } from "@/types/ChipConfig.ts";

import {
  mdiCalendarBlankOutline,
  mdiCalendarCheckOutline,
  mdiCalendarEditOutline,
  mdiCalendarQuestionOutline,
  mdiCalendarRemoveOutline,
  mdiDoorOpen,
  mdiTimerSand,
} from "@mdi/js";

import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend";
import { type Role } from "@/types/Role.ts";

// Color keys correspond to custom theme colors registered in vuetify.ts
const NEW_COLOR = "new";
const IN_PROGRESS_COLOR = "requested";
const COORDINATION_COLOR = "coordination";
const APPROVED_COLOR = "approved";
const OBSOLETE_COLOR = "canceled";

const NEW_STATUS_STYLE: ChipConfig = {
  color: NEW_COLOR,
  text: "domain.booking.status.new",
  icon: mdiCalendarBlankOutline,
};

const ORGANIZER_APPROVED_STATUS_STYLE: ChipConfig = {
  color: APPROVED_COLOR,
  text: "domain.booking.status.organizerApproved",
  icon: mdiCalendarCheckOutline,
};
const UNFEASIBLE_STATUS_STYLE: ChipConfig = {
  color: OBSOLETE_COLOR,
  text: "domain.booking.status.unfeasible",
  icon: mdiCalendarRemoveOutline,
};
const CANCELED_STATUS_STYLE: ChipConfig = {
  color: OBSOLETE_COLOR,
  text: "domain.booking.status.canceled",
  icon: mdiCalendarRemoveOutline,
};

const ADMIN_STATUS_STYLES: Record<BookingStatus, ChipConfig> = {
  NEW: NEW_STATUS_STYLE,
  ROOM_APPROVED: {
    color: IN_PROGRESS_COLOR,
    text: "domain.booking.status.roomApproved.roomOrganizer",
    icon: mdiDoorOpen,
  },
  ROOM_CHANGED: {
    color: IN_PROGRESS_COLOR,
    text: "domain.booking.status.roomChanged.readerAndRoomOrganizer",
    icon: mdiCalendarEditOutline,
  },
  COORDINATION_NEEDED: {
    color: COORDINATION_COLOR,
    text: "domain.booking.status.coordinationNeeded.organizer",
    icon: mdiCalendarQuestionOutline,
  },
  ORGANIZER_APPROVED: ORGANIZER_APPROVED_STATUS_STYLE,
  ORGANIZER_CHANGED: {
    color: IN_PROGRESS_COLOR,
    text: "domain.booking.status.organizerChanged.roomOrganizer",
    icon: mdiCalendarEditOutline,
  },
  UNFEASIBLE: UNFEASIBLE_STATUS_STYLE,
  CANCELED: CANCELED_STATUS_STYLE,
};

export const ROLE_STATUS_STYLES: Record<
  Role,
  Record<BookingStatus, ChipConfig>
> = {
  raumadmin: ADMIN_STATUS_STYLES,
  raumbuchung: ADMIN_STATUS_STYLES,
  terminorganisator: {
    NEW: NEW_STATUS_STYLE,
    ROOM_APPROVED: {
      color: IN_PROGRESS_COLOR,
      text: "domain.booking.status.roomApproved.userAndBookingOrganizer",
      icon: mdiCalendarBlankOutline,
    },
    ROOM_CHANGED: {
      color: NEW_COLOR,
      text: "domain.booking.status.roomChanged.bookingOrganizer",
      icon: mdiCalendarEditOutline,
    },
    COORDINATION_NEEDED: {
      color: COORDINATION_COLOR,
      text: "domain.booking.status.coordinationNeeded.organizer",
      icon: mdiCalendarQuestionOutline,
    },
    ORGANIZER_APPROVED: ORGANIZER_APPROVED_STATUS_STYLE,
    ORGANIZER_CHANGED: {
      color: COORDINATION_COLOR,
      text: "domain.booking.status.organizerChanged.readerAndBookingOrganizer",
      icon: mdiCalendarEditOutline,
    },
    UNFEASIBLE: UNFEASIBLE_STATUS_STYLE,
    CANCELED: CANCELED_STATUS_STYLE,
  },
  leseberechtigt: {
    NEW: NEW_STATUS_STYLE,
    ROOM_APPROVED: {
      color: IN_PROGRESS_COLOR,
      text: "domain.booking.status.roomApproved.userAndReader",
      icon: mdiTimerSand,
    },
    ROOM_CHANGED: {
      color: IN_PROGRESS_COLOR,
      text: "domain.booking.status.roomChanged.readerAndRoomOrganizer",
      icon: mdiCalendarEditOutline,
    },
    COORDINATION_NEEDED: {
      color: COORDINATION_COLOR,
      text: "domain.booking.status.coordinationNeeded.userAndReader",
      icon: mdiTimerSand,
    },
    ORGANIZER_APPROVED: ORGANIZER_APPROVED_STATUS_STYLE,
    ORGANIZER_CHANGED: {
      color: IN_PROGRESS_COLOR,
      text: "domain.booking.status.organizerChanged.readerAndBookingOrganizer",
      icon: mdiCalendarEditOutline,
    },
    UNFEASIBLE: UNFEASIBLE_STATUS_STYLE,
    CANCELED: CANCELED_STATUS_STYLE,
  },
  anwender: {
    NEW: NEW_STATUS_STYLE,
    ROOM_APPROVED: {
      color: IN_PROGRESS_COLOR,
      text: "domain.booking.status.roomApproved.userAndReader",
      icon: mdiTimerSand,
    },
    ROOM_CHANGED: {
      color: NEW_COLOR,
      text: "domain.booking.status.roomChanged.userAndBookingOrganizer",
      icon: mdiTimerSand,
    },
    COORDINATION_NEEDED: {
      color: COORDINATION_COLOR,
      text: "domain.booking.status.coordinationNeeded.userAndReader",
      icon: mdiTimerSand,
    },
    ORGANIZER_APPROVED: ORGANIZER_APPROVED_STATUS_STYLE,
    ORGANIZER_CHANGED: {
      color: IN_PROGRESS_COLOR,
      text: "domain.booking.status.organizerChanged.user",
      icon: mdiTimerSand,
    },
    UNFEASIBLE: UNFEASIBLE_STATUS_STYLE,
    CANCELED: CANCELED_STATUS_STYLE,
  },
};

export const EMPTY_BOOKING_STATUS_DATA: BookingStatusDTO = {
  currentStatus: BookingStatusDTOCurrentStatusEnum.NEW,
  nextPossibleStatus: [BookingStatusDTOCurrentStatusEnum.NEW],
};
