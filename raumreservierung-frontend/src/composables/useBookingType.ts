import { computed } from "vue";
import { useI18n } from "vue-i18n";

import { BookingRequestDTOBookingTypeEnum } from "@/api/raumreservierung-backend";

export function useBookingType() {
  const { t } = useI18n();

  const bookingTypeOptions = computed(() => [
    {
      text: t("domain.booking.types.default"),
      value: BookingRequestDTOBookingTypeEnum.DEFAULT,
    },
    {
      text: t("domain.booking.types.free"),
      value: BookingRequestDTOBookingTypeEnum.FREE,
    },
    {
      text: t("domain.booking.types.service"),
      value: BookingRequestDTOBookingTypeEnum.SERVICE,
    },
  ]);

  const getBookingTypeText = (value?: string) => {
    if (!value) return "";
    const option = bookingTypeOptions.value.find((opt) => opt.value === value);
    return option ? option.text : value;
  };

  return {
    bookingTypeOptions,
    getBookingTypeText,
  };
}
