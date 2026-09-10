import type { BookingDetailResponseDTO, BookingListResponseDTO, BookingRequestDTO, GetBookingsByPageableAndFilterStatusEnum } from "@/api/raumreservierung-backend";
import type { StatusGroup, StatusGroupKey } from "@/constants/BookingStatus.ts";
import type { ChipConfig } from "@/types/ChipConfig.ts";
import type { ComputedRef, MaybeRefOrGetter } from "vue";



import { computed, toValue } from "vue";
import { useI18n } from "vue-i18n";



import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend/models/BookingStatusDTO";
import { FALLBACK_CONFIG, ROLE_STATUS_GROUPS } from "@/constants/BookingStatus.ts";
import { useUserStore } from "@/stores/user.ts";


export function useIsBookingEditable(): (
  booking: BookingRequestDTO | BookingDetailResponseDTO | BookingListResponseDTO |  undefined
) => boolean;

export function useIsBookingEditable(
  booking: MaybeRefOrGetter<
    | BookingRequestDTO
    | BookingDetailResponseDTO
    | BookingListResponseDTO
    | undefined
  >
): ComputedRef<boolean>;

export function useIsBookingEditable(
  booking?: MaybeRefOrGetter<
    | BookingRequestDTO
    | BookingListResponseDTO
    | BookingDetailResponseDTO
    | undefined
  >
) {
  const evaluateIsEditable = (
    booking:
      | BookingRequestDTO
      | BookingDetailResponseDTO
      | BookingListResponseDTO
      | undefined
  ) => {
    if (!booking) {
      return false;
    }

    const status =
      typeof booking.status === "string"
        ? booking.status
        : booking.status.currentStatus;

    return status !== "CANCELED" && status !== "UNFEASIBLE";
  };

  if (booking === undefined) {
    return evaluateIsEditable;
  }

  const bookingRef = toValue(booking);

  return computed(() => evaluateIsEditable(bookingRef));
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

  // NEU: Direkte Auflösung der Farbe für einen statischen Status-String
  const resolveColor = (status: string | undefined): string => {
    const group = findGroup(status);
    return group?.config.color ?? FALLBACK_CONFIG.color;
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
    resolveColor,
  };
}
