import type { ChipConfig } from "@/types/ChipConfig.ts";
import type { Role } from "@/types/Role.ts";

import { computed } from "vue";

import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend/models/BookingStatusDTO";
import { ROLE_STATUS_STYLES } from "@/constants/BookingStatus.ts";
import { useUserStore } from "@/stores/user.ts";
import { isRole } from "@/types/Role.ts";

export function useBookingStatusConfig() {
  const userStore = useUserStore();

  const activeRole = computed<Role>(() => {
    const primaryRole = userStore.user?.user_roles?.[0] ?? "";
    return isRole(primaryRole) ? primaryRole : "anwender";
  });

  const UNKNOWN_STYLE: ChipConfig = {
    color: "default",
    text: "Unbekannt",
    icon: "mdiHelp",
  };

  const statusConfig = computed(() => {
    const currentRole = activeRole.value;
    const roleStyles = ROLE_STATUS_STYLES[currentRole] || {};

    return {
      get: (status: string | undefined): ChipConfig => {
        if (!status) return UNKNOWN_STYLE;

        const upperStatus =
          status.toUpperCase() as BookingStatusDTOCurrentStatusEnum;
        return (
          roleStyles[upperStatus] || {
            color: "default",
            text: status,
            icon: "mdiHelp",
          }
        );
      },
    };
  });

  return {
    statusConfig,
  };
}
