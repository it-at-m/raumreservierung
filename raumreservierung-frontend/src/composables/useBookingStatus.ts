import type { ChipConfig } from "@/types/ChipConfig.ts";

import { useI18n } from "vue-i18n";

import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend/models/BookingStatusDTO";
import {
  FALLBACK_CONFIG,
  ROLE_STATUS_STYLES,
} from "@/constants/BookingStatus.ts";
import { useUserStore } from "@/stores/user.ts";

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
    const baseStyle = ROLE_STATUS_STYLES[activeRole][upperStatus];

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
