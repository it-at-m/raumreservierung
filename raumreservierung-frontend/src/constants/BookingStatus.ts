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

const NEW_COLOR = "new";
const IN_PROGRESS_COLOR = "requested";
const COORDINATION_COLOR = "coordination";
const APPROVED_COLOR = "approved";
const OBSOLETE_COLOR = "canceled";

const NEW_STATUS_STYLE: ChipConfig = {
  color: NEW_COLOR,
  text: "Angefragt",
  icon: mdiCalendarBlankOutline,
};

const ORGANIZER_APPROVED_STATUS_STYLE: ChipConfig = {
  color: APPROVED_COLOR,
  text: "Gebucht",
  icon: mdiCalendarCheckOutline,
};
const UNFEASIBLE_STATUS_STYLE: ChipConfig = {
  color: OBSOLETE_COLOR,
  text: "Abgelehnt",
  icon: mdiCalendarRemoveOutline,
};
const CANCELED_STATUS_STYLE: ChipConfig = {
  color: OBSOLETE_COLOR,
  text: "Storniert",
  icon: mdiCalendarRemoveOutline,
};

const ADMIN_STATUS_STYLES: Record<BookingStatus, ChipConfig> = {
  NEW: NEW_STATUS_STYLE,
  ROOM_APPROVED: {
    color: IN_PROGRESS_COLOR,
    text: "Vorgemerkt",
    icon: mdiDoorOpen,
  },
  ROOM_CHANGED: {
    color: IN_PROGRESS_COLOR,
    text: "Änderung",
    icon: mdiCalendarEditOutline,
  },
  COORDINATION_NEEDED: {
    color: COORDINATION_COLOR,
    text: "In Abstimmung",
    icon: mdiCalendarQuestionOutline,
  },
  ORGANIZER_APPROVED: ORGANIZER_APPROVED_STATUS_STYLE,
  ORGANIZER_CHANGED: {
    color: IN_PROGRESS_COLOR,
    text: "Vorgemerkt",
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
      text: "Neu",
      icon: mdiCalendarBlankOutline,
    },
    ROOM_CHANGED: {
      color: NEW_COLOR,
      text: "Angefragt",
      icon: mdiCalendarEditOutline,
    },
    COORDINATION_NEEDED: {
      color: COORDINATION_COLOR,
      text: "In Abstimmung",
      icon: mdiCalendarQuestionOutline,
    },
    ORGANIZER_APPROVED: ORGANIZER_APPROVED_STATUS_STYLE,
    ORGANIZER_CHANGED: {
      color: COORDINATION_COLOR,
      text: "Änderung",
      icon: mdiCalendarEditOutline,
    },
    UNFEASIBLE: UNFEASIBLE_STATUS_STYLE,
    CANCELED: CANCELED_STATUS_STYLE,
  },
  leseberechtigt: {
    NEW: NEW_STATUS_STYLE,
    ROOM_APPROVED: {
      color: IN_PROGRESS_COLOR,
      text: "In Bearbeitung",
      icon: mdiTimerSand,
    },
    ROOM_CHANGED: {
      color: IN_PROGRESS_COLOR,
      text: "Änderung",
      icon: mdiCalendarEditOutline,
    },
    COORDINATION_NEEDED: {
      color: COORDINATION_COLOR,
      text: "In Bearbeitung",
      icon: mdiTimerSand,
    },
    ORGANIZER_APPROVED: ORGANIZER_APPROVED_STATUS_STYLE,
    ORGANIZER_CHANGED: {
      color: IN_PROGRESS_COLOR,
      text: "Änderung",
      icon: mdiCalendarEditOutline,
    },
    UNFEASIBLE: UNFEASIBLE_STATUS_STYLE,
    CANCELED: CANCELED_STATUS_STYLE,
  },
  anwender: {
    NEW: NEW_STATUS_STYLE,
    ROOM_APPROVED: {
      color: IN_PROGRESS_COLOR,
      text: "In Bearbeitung",
      icon: mdiTimerSand,
    },
    ROOM_CHANGED: {
      color: NEW_COLOR,
      text: "Angefragt",
      icon: mdiTimerSand,
    },
    COORDINATION_NEEDED: {
      color: COORDINATION_COLOR,
      text: "In Bearbeitung",
      icon: mdiTimerSand,
    },
    ORGANIZER_APPROVED: ORGANIZER_APPROVED_STATUS_STYLE,
    ORGANIZER_CHANGED: {
      color: IN_PROGRESS_COLOR,
      text: "In Bearbeitung",
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
