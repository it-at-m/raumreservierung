import type { GetBookingsByPageableAndFilterStatusEnum } from "@/api/raumreservierung-backend";
import type { StatusGroup, StatusGroupKey } from "@/constants/BookingStatus.ts";
import type { ChipConfig } from "@/types/ChipConfig.ts";

import { computed } from "vue";
import { useI18n } from "vue-i18n";

import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend/models/BookingStatusDTO";
import {
  FALLBACK_CONFIG,
  ROLE_STATUS_GROUPS,
} from "@/constants/BookingStatus.ts";
import { useUserStore } from "@/stores/user.ts";

export function useBookingStatusConfig() {
  const { t } = useI18n();
  const userStore = useUserStore();

  const activeRole = userStore.user?.user_roles;

  const applyText = (config: ChipConfig): ChipConfig => {
    return { ...config, text: t(config.text) };
  };

  const getStatusConfig = (status: string | undefined): ChipConfig =>
    applyText(findGroup(status)?.config ?? FALLBACK_CONFIG);

  const statusGroups = computed<StatusGroup[]>(() =>
    activeRole ? ROLE_STATUS_GROUPS[activeRole] : []
  );

  const findGroup = (status: string | undefined) => {
    if (!status) {
      return undefined;
    }
    const upperStatus =
      status.toUpperCase() as BookingStatusDTOCurrentStatusEnum;
    return statusGroups.value.find((group) =>
      group.status.includes(upperStatus)
    );
  };

  const getStatusGroupKey = (status: string): StatusGroupKey | string =>
    findGroup(status)?.key ?? status;

  const expandStatus = (
    selected: string[]
  ): GetBookingsByPageableAndFilterStatusEnum[] => [
    ...new Set(
      selected
        .flatMap((status) => findGroup(status)?.status ?? [status])
        .map(
          (status) =>
            status.toUpperCase() as GetBookingsByPageableAndFilterStatusEnum
        )
    ),
  ];

  return {
    getStatusConfig,
    getStatusGroupKey,
    expandStatus,
    statusGroups,
  };
}
