import type {
  BookingStatusDTOCurrentStatusEnum as BookingStatus,
  BookingStatusDTO,
} from "@/api/raumreservierung-backend";
import type { ChipConfig } from "@/types/ChipConfig.ts";

import {
  mdiCalendarBlankOutline,
  mdiCalendarCheckOutline,
  mdiCalendarHeartOutline,
  mdiCalendarQuestionOutline,
  mdiCalendarRefreshOutline,
  mdiCalendarRemoveOutline,
  mdiHelp,
  mdiTimerSand,
} from "@mdi/js";

import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend";
import { type Role } from "@/types/Role.ts";

// Color keys correspond to custom theme colors registered in vuetify.ts
const NEW_COLOR = "statusNew";
const IN_PROGRESS_COLOR = "statusRequested";
const COORDINATION_COLOR = "statusCoordination";
const APPROVED_COLOR = "statusApproved";
const OBSOLETE_COLOR = "statusCanceled";

export type StatusGroupKey =
  | "requested"
  | "new"
  | "reserved"
  | "inProgress"
  | "inCoordination"
  | "changed"
  | "approved"
  | "unfeasible"
  | "canceled";

const NEW_STATUS_STYLE: ChipConfig = {
  color: NEW_COLOR,
  text: "domain.booking.status.requested",
  icon: mdiCalendarBlankOutline,
};

const COORDINATION_NEEDED_STATUS_STYLE: ChipConfig = {
  color: COORDINATION_COLOR,
  text: "domain.booking.status.inCoordination",
  icon: mdiCalendarQuestionOutline,
};

const IN_PROGRESS_STATUS_STYLE: ChipConfig = {
  color: IN_PROGRESS_COLOR,
  text: "domain.booking.status.inProgress",
  icon: mdiTimerSand,
};

const CHANGED_STATUS_STYLE: ChipConfig = {
  color: IN_PROGRESS_COLOR,
  text: "domain.booking.status.changed",
  icon: mdiCalendarRefreshOutline,
};

const ORGANIZER_APPROVED_STATUS_STYLE: ChipConfig = {
  color: APPROVED_COLOR,
  text: "domain.booking.status.approved",
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

export const FALLBACK_CONFIG: ChipConfig = {
  color: "default",
  text: "domain.booking.status.unknown",
  icon: mdiHelp,
};

export const EMPTY_BOOKING_STATUS_DATA: BookingStatusDTO = {
  currentStatus: BookingStatusDTOCurrentStatusEnum.NEW,
  nextPossibleStatus: [BookingStatusDTOCurrentStatusEnum.NEW],
};

export interface StatusGroup {
  key: StatusGroupKey;
  status: BookingStatus[];
  config: ChipConfig;
}
const status = BookingStatusDTOCurrentStatusEnum;
const ANWENDER_GROUPS: StatusGroup[] = [
  {
    key: "requested",
    status: [status.NEW, status.ROOM_CHANGED],
    config: NEW_STATUS_STYLE,
  },
  {
    key: "inProgress",
    status: [
      status.ROOM_APPROVED,
      status.ORGANIZER_CHANGED,
      status.COORDINATION_NEEDED,
    ],
    config: IN_PROGRESS_STATUS_STYLE,
  },
  {
    key: "approved",
    status: [status.ORGANIZER_APPROVED],
    config: ORGANIZER_APPROVED_STATUS_STYLE,
  },
  {
    key: "unfeasible",
    status: [status.UNFEASIBLE],
    config: UNFEASIBLE_STATUS_STYLE,
  },
  {
    key: "canceled",
    status: [status.CANCELED],
    config: CANCELED_STATUS_STYLE,
  },
];
const LESEBERECHTIGT_GROUPS: StatusGroup[] = [
  {
    key: "requested",
    status: [status.NEW],
    config: NEW_STATUS_STYLE,
  },
  {
    key: "inProgress",
    status: [status.ROOM_APPROVED, status.COORDINATION_NEEDED],
    config: IN_PROGRESS_STATUS_STYLE,
  },
  {
    key: "changed",
    status: [status.ORGANIZER_CHANGED, status.ROOM_CHANGED],
    config: CHANGED_STATUS_STYLE,
  },
  {
    key: "approved",
    status: [status.ORGANIZER_APPROVED],
    config: ORGANIZER_APPROVED_STATUS_STYLE,
  },
  {
    key: "unfeasible",
    status: [status.UNFEASIBLE],
    config: UNFEASIBLE_STATUS_STYLE,
  },
  {
    key: "canceled",
    status: [status.CANCELED],
    config: CANCELED_STATUS_STYLE,
  },
];
const TERMINORGANISATOR_GROUPS: StatusGroup[] = [
  {
    key: "requested",
    status: [status.NEW, status.ROOM_CHANGED],
    config: NEW_STATUS_STYLE,
  },
  {
    key: "new",
    status: [status.ROOM_APPROVED],
    config: {
      color: IN_PROGRESS_COLOR,
      text: "domain.booking.status.new",
      icon: mdiCalendarBlankOutline,
    },
  },
  {
    key: "inCoordination",
    status: [status.COORDINATION_NEEDED],
    config: COORDINATION_NEEDED_STATUS_STYLE,
  },
  {
    key: "changed",
    status: [status.ORGANIZER_CHANGED],
    config: CHANGED_STATUS_STYLE,
  },
  {
    key: "approved",
    status: [status.ORGANIZER_APPROVED],
    config: ORGANIZER_APPROVED_STATUS_STYLE,
  },
  {
    key: "unfeasible",
    status: [status.UNFEASIBLE],
    config: UNFEASIBLE_STATUS_STYLE,
  },
  {
    key: "canceled",
    status: [status.CANCELED],
    config: CANCELED_STATUS_STYLE,
  },
];
const ADMIN_GROUPS: StatusGroup[] = [
  {
    key: "requested",
    status: [status.NEW],
    config: NEW_STATUS_STYLE,
  },
  {
    key: "reserved",
    status: [status.ROOM_APPROVED, status.ORGANIZER_CHANGED],
    config: {
      color: IN_PROGRESS_COLOR,
      text: "domain.booking.status.reserved",
      icon: mdiCalendarHeartOutline,
    },
  },
  {
    key: "inCoordination",
    status: [status.COORDINATION_NEEDED],
    config: COORDINATION_NEEDED_STATUS_STYLE,
  },
  {
    key: "changed",
    status: [status.ROOM_CHANGED],
    config: { ...CHANGED_STATUS_STYLE, color: NEW_COLOR },
  },
  {
    key: "approved",
    status: [status.ORGANIZER_APPROVED],
    config: ORGANIZER_APPROVED_STATUS_STYLE,
  },
  {
    key: "unfeasible",
    status: [status.UNFEASIBLE],
    config: UNFEASIBLE_STATUS_STYLE,
  },
  {
    key: "canceled",
    status: [status.CANCELED],
    config: CANCELED_STATUS_STYLE,
  },
];

export const ROLE_STATUS_GROUPS: Record<Role, StatusGroup[]> = {
  raumadmin: ADMIN_GROUPS,
  raumbuchung: ADMIN_GROUPS,
  terminorganisator: TERMINORGANISATOR_GROUPS,
  leseberechtigt: LESEBERECHTIGT_GROUPS,
  anwender: ANWENDER_GROUPS,
};
