import type { BookingDetailResponseDTOBookingTypeEnum } from "@/api/raumreservierung-backend";
import type { ChipConfig } from "@/types/ChipConfig.ts";
import type { MaybeRefOrGetter } from "vue";

import { computed, toValue } from "vue";

import { BOOKING_TYPE_STYLES } from "@/constants/BookingType.ts";

export function useBookingTypeStyle(
  bookingType: MaybeRefOrGetter<
    BookingDetailResponseDTOBookingTypeEnum | undefined
  >
) {
  const currentTypeStyle = computed<ChipConfig>(() => {
    const typeValue = toValue(bookingType);

    if (!typeValue || !(typeValue in BOOKING_TYPE_STYLES)) {
      return BOOKING_TYPE_STYLES.DEFAULT;
    }
    return BOOKING_TYPE_STYLES[typeValue];
  });

  const bookingTypes = computed(() => {
    return Object.entries(BOOKING_TYPE_STYLES).map(([key, config]) => ({
      text: config.text,
      value: key,
      icon: config.icon,
      color: config.color,
    }));
  });

  return {
    currentTypeStyle,
    bookingTypes,
  };
}
