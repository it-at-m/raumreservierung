import type {
  BookingDetailResponseDTO,
  BookingRequestDTO,
  GetBookingsByPageableAndFilterStatusEnum,
} from "@/api/raumreservierung-backend";
import type { StatusGroup, StatusGroupKey } from "@/constants/BookingStatus.ts";
import type { ChipConfig } from "@/types/ChipConfig.ts";
import type { MaybeRefOrGetter } from "vue";

import { computed, toValue } from "vue";
import { useI18n } from "vue-i18n";

import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend/models/BookingStatusDTO";
import {
  FALLBACK_CONFIG,
  ROLE_STATUS_GROUPS,
} from "@/constants/BookingStatus.ts";
import { useUserStore } from "@/stores/user.ts";

export const useIsBookingEditable = (
  booking: MaybeRefOrGetter<
    BookingRequestDTO | BookingDetailResponseDTO | undefined
  >
) => {
  return computed(() => {
    const bookingRef = toValue(booking);

    if (!bookingRef) {
      return false;
    }

    const status =
      typeof bookingRef.status === "string"
        ? bookingRef.status
        : bookingRef.status.currentStatus;

    return status !== "CANCELED" && status !== "UNFEASIBLE";
  });
};

export function useBookingStatusConfig(
  statusRef?: MaybeRefOrGetter<string | undefined>
) {
  const { t } = useI18n();
  const userStore = useUserStore();

  const activeRole = computed(() => userStore.user?.user_roles);

  const statusGroups = computed<StatusGroup[]>(() =>
    activeRole.value ? ROLE_STATUS_GROUPS[activeRole.value] : []
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

  const applyText = (config: ChipConfig): ChipConfig => {
    return { ...config, text: t(config.text) };
  };

  const config = computed<ChipConfig>(() => {
    const status = toValue(statusRef);
    return applyText(findGroup(status)?.config ?? FALLBACK_CONFIG);
  });

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
    config,
    getStatusGroupKey,
    expandStatus,
    statusGroups,
  };
}
