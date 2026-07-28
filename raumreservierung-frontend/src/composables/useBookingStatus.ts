import type { ChipConfig } from "@/types/ChipConfig.ts";

import { useI18n } from "vue-i18n";

import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend/models/BookingStatusDTO";
import { ROLE_STATUS_STYLES } from "@/constants/BookingStatus.ts";
import { useUserStore } from "@/stores/user.ts";

const FALLBACK_CONFIG: ChipConfig = {
  color: "default",
  text: "domain.booking.status.unknown",
  icon: "mdiHelp",
};

export function useBookingStatusConfig() {
  const userStore = useUserStore();
  const { t } = useI18n();

  const activeRole = userStore.user?.user_roles;

  const getStatusConfig = (status: string | undefined): ChipConfig => {
    if (!status || !activeRole) {
      return FALLBACK_CONFIG;
    }

    const upperStatus =
      status.toUpperCase() as BookingStatusDTOCurrentStatusEnum;
    const roleStyles = ROLE_STATUS_STYLES[activeRole];
    const baseStyle = roleStyles[upperStatus];

    if (!baseStyle) {
      return FALLBACK_CONFIG;
    }

    return {
      ...baseStyle,
      text: t(baseStyle.text),
    };
  };

  return {
    getStatusConfig,
  };
}
