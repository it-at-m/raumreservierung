import type { BookingDetailResponseDTOBookingTypeEnum } from "@/api/raumreservierung-backend";
import type { ChipConfig } from "@/types/ChipConfig.ts";

import {
  mdiCalendarCheckOutline,
  mdiCalendarTextOutline,
  mdiWrenchClockOutline,
} from "@mdi/js";

const DEFAULT_TYPE_STYLE: ChipConfig = {
  color: "default",
  text: "Normal",
  icon: mdiCalendarTextOutline,
};
const FREE_TYPE_STYLE: ChipConfig = {
  color: "free",
  text: "Frei",
  icon: mdiCalendarCheckOutline,
};
const SERVICE_TYPE_STYLE: ChipConfig = {
  color: "service",
  text: "Service",
  icon: mdiWrenchClockOutline,
};

export const BOOKING_TYPE_STYLES: Record<
  BookingDetailResponseDTOBookingTypeEnum,
  ChipConfig
> = {
  DEFAULT: DEFAULT_TYPE_STYLE,
  FREE: FREE_TYPE_STYLE,
  SERVICE: SERVICE_TYPE_STYLE,
};
