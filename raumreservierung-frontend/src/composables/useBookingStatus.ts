import type { Role } from "@/types/Role.ts";

import {
  mdiCalendarCheckOutline,
  mdiCalendarEditOutline,
  mdiCalendarQuestionOutline,
  mdiCalendarRemoveOutline,
  mdiDoorOpen,
} from "@mdi/js";
import { computed } from "vue";

import { type BookingStatusDTOCurrentStatusEnum as BookingStatus } from "@/api/raumreservierung-backend/models/BookingStatusDTO";
import { useUserStore } from "@/stores/user.ts";
import { isRole } from "@/types/Role.ts";

export interface ChipStyle {
  color: string;
  text: string;
  button: ButtonStyle;
}

export interface ButtonStyle {
  icon: string;
  text: string;
}

const GREY_COLOR = "grey";
const YELLOW_COLOR = "info";
const ORANGE_COLOR = "warning";
const GREEN_COLOR = "success";
const RED_COLOR = "error";

const EMPTY_BUTTON: ButtonStyle = { icon: "", text: "" };
const CHANGE_BUTTON: ButtonStyle = {
  icon: mdiCalendarEditOutline,
  text: "Änderung",
};
const BOOK_BUTTON: ButtonStyle = {
  icon: mdiCalendarCheckOutline,
  text: "Buchen",
};
const COORDINATION_REQUIRED_BUTTON: ButtonStyle = {
  icon: mdiCalendarQuestionOutline,
  text: "Abstimmung",
};
const ROOM_AVAILABLE_BUTTON: ButtonStyle = {
  icon: mdiDoorOpen,
  text: "Raum verfügbar",
};
const DECLINE_BUTTON: ButtonStyle = {
  icon: mdiCalendarRemoveOutline,
  text: "Ablehnen",
};

const NEW_STATUS_STYLE: ChipStyle = {
  color: GREY_COLOR,
  text: "angefragt",
  button: EMPTY_BUTTON,
};

const ORGANIZER_APPROVED_STATUS_STYLE: ChipStyle = {
  color: GREEN_COLOR,
  text: "gebucht",
  button: EMPTY_BUTTON,
};
const UNFEASIBLE_STATUS_STYLE: ChipStyle = {
  color: RED_COLOR,
  text: "abgelehnt",
  button: EMPTY_BUTTON,
};
const CANCELLED_STATUS_STYLE: ChipStyle = {
  color: RED_COLOR,
  text: "storniert",
  button: { icon: mdiCalendarRemoveOutline, text: "Stornieren" },
};

const ADMIN_STATUS_STYLES: Record<BookingStatus, ChipStyle> = {
  NEW: NEW_STATUS_STYLE,
  ROOM_APPROVED: {
    color: YELLOW_COLOR,
    text: "vorgemerkt",
    button: ROOM_AVAILABLE_BUTTON,
  },
  ROOM_CHANGED: {
    color: YELLOW_COLOR,
    text: "Änderung",
    button: CHANGE_BUTTON,
  },
  COORDINATION_NEEDED: {
    color: ORANGE_COLOR,
    text: "in Abstimmung",
    button: COORDINATION_REQUIRED_BUTTON,
  },
  ORGANIZER_APPROVED: {
    ...ORGANIZER_APPROVED_STATUS_STYLE,
    button: BOOK_BUTTON,
  },
  ORGANIZER_CHANGED: {
    color: YELLOW_COLOR,
    text: "vorgemerkt",
    button: ROOM_AVAILABLE_BUTTON,
  },
  UNFEASIBLE: { ...UNFEASIBLE_STATUS_STYLE, button: DECLINE_BUTTON },
  CANCELLED: CANCELLED_STATUS_STYLE,
};
export const ROLE_STATUS_STYLES: Record<
  Role,
  Record<BookingStatus, ChipStyle>
> = {
  raumadmin: ADMIN_STATUS_STYLES,
  raumbuchung: ADMIN_STATUS_STYLES,
  terminorganisator: {
    NEW: {
      color: GREY_COLOR,
      text: "should not be visible",
      button: EMPTY_BUTTON,
    },
    ROOM_APPROVED: { color: YELLOW_COLOR, text: "neu", button: EMPTY_BUTTON },
    ROOM_CHANGED: {
      color: GREY_COLOR,
      text: "angefragt",
      button: CHANGE_BUTTON,
    },
    COORDINATION_NEEDED: {
      color: ORANGE_COLOR,
      text: "in Abstimmung",
      button: COORDINATION_REQUIRED_BUTTON,
    },
    ORGANIZER_APPROVED: {
      ...ORGANIZER_APPROVED_STATUS_STYLE,
      button: BOOK_BUTTON,
    },
    ORGANIZER_CHANGED: {
      color: ORANGE_COLOR,
      text: "Änderung",
      button: EMPTY_BUTTON,
    },
    UNFEASIBLE: UNFEASIBLE_STATUS_STYLE,
    CANCELLED: CANCELLED_STATUS_STYLE,
  },
  leseberechtigt: {
    NEW: NEW_STATUS_STYLE,
    ROOM_APPROVED: {
      color: YELLOW_COLOR,
      text: "in Bearbeitung",
      button: EMPTY_BUTTON,
    },
    ROOM_CHANGED: {
      color: YELLOW_COLOR,
      text: "Änderung",
      button: CHANGE_BUTTON,
    },
    COORDINATION_NEEDED: {
      color: ORANGE_COLOR,
      text: "in Bearbeitung",
      button: EMPTY_BUTTON,
    },
    ORGANIZER_APPROVED: ORGANIZER_APPROVED_STATUS_STYLE,
    ORGANIZER_CHANGED: {
      color: YELLOW_COLOR,
      text: "Änderung",
      button: EMPTY_BUTTON,
    },
    UNFEASIBLE: UNFEASIBLE_STATUS_STYLE,
    CANCELLED: CANCELLED_STATUS_STYLE,
  },
  anwender: {
    NEW: NEW_STATUS_STYLE,
    ROOM_APPROVED: {
      color: YELLOW_COLOR,
      text: "in Bearbeitung",
      button: EMPTY_BUTTON,
    },
    ROOM_CHANGED: {
      color: GREY_COLOR,
      text: "angefragt",
      button: CHANGE_BUTTON,
    },
    COORDINATION_NEEDED: {
      color: ORANGE_COLOR,
      text: "in Bearbeitung",
      button: EMPTY_BUTTON,
    },
    ORGANIZER_APPROVED: ORGANIZER_APPROVED_STATUS_STYLE,
    ORGANIZER_CHANGED: {
      color: YELLOW_COLOR,
      text: "in Bearbeitung",
      button: EMPTY_BUTTON,
    },
    UNFEASIBLE: UNFEASIBLE_STATUS_STYLE,
    CANCELLED: CANCELLED_STATUS_STYLE,
  },
};

export function useBookingStatusStyles() {
  const userStore = useUserStore();

  const activeRole = computed<Role>(() => {
    const primaryRole = userStore.user?.user_roles?.[0] ?? "";
    return isRole(primaryRole) ? primaryRole : "anwender";
  });

  const getStatusStyle = (status: string | undefined): ChipStyle => {
    if (!status) {
      return { color: "default", text: "Unbekannt", button: EMPTY_BUTTON };
    }
    const currentRole = activeRole.value;
    const upperStatus = status.toUpperCase() as BookingStatus;

    const defaultStyle: ChipStyle = {
      color: "default",
      text: status,
      button: EMPTY_BUTTON,
    };

    return ROLE_STATUS_STYLES[currentRole]?.[upperStatus] || defaultStyle;
  };

  return {
    activeRole,
    getStatusStyle,
  };
}
