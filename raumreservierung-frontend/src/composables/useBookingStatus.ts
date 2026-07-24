import type { ChipConfig } from "@/types/ChipConfig.ts";
import type { Role } from "@/types/Role.ts";

import { computed } from "vue";
import { useI18n } from "vue-i18n";

import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend/models/BookingStatusDTO";
import { ROLE_STATUS_STYLES } from "@/constants/BookingStatus.ts";
import { useUserStore } from "@/stores/user.ts";
import { isRole } from "@/types/Role.ts";

export function useBookingStatusConfig() {
  const userStore = useUserStore();
  const { t } = useI18n();

  const activeRole = computed<Role>(() => {
    const primaryRole = userStore.user?.user_roles?.[0] ?? "";
    return isRole(primaryRole) ? primaryRole : "anwender";
  });

  const getUnknownConfig = (): ChipConfig => ({
    color: "default",
    text: t("domain.booking.status.unknown"),
    icon: "mdiHelp",
  });

  const getStatusConfig = (status: string | undefined): ChipConfig => {
    if (!status) {
      return getUnknownConfig();
    }

    const upperStatus =
      status.toUpperCase() as BookingStatusDTOCurrentStatusEnum;
    const roleStyles = ROLE_STATUS_STYLES[activeRole.value] || {};
    const baseStyle = roleStyles[upperStatus];

    if (!baseStyle) {
      return getUnknownConfig();
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
