import type { BookingDetailResponseDTOBookingTypeEnum } from "@/api/raumreservierung-backend";
import type { MaybeRefOrGetter } from "vue";

import { computed, toValue } from "vue";
import { useI18n } from "vue-i18n";

import { BookingRequestDTOBookingTypeEnum } from "@/api/raumreservierung-backend";

export function useBookingType(
  type?: MaybeRefOrGetter<BookingDetailResponseDTOBookingTypeEnum | undefined>
) {
  const { t } = useI18n();

  const bookingTypeOptions = computed(() => [
    {
      title: t("domain.booking.types.default"),
      value: BookingRequestDTOBookingTypeEnum.DEFAULT,
    },
    {
      title: t("domain.booking.types.free"),
      value: BookingRequestDTOBookingTypeEnum.FREE,
    },
    {
      title: t("domain.booking.types.service"),
      value: BookingRequestDTOBookingTypeEnum.SERVICE,
    },
  ]);

  const bookingTypeText = computed(
    () =>
      bookingTypeOptions.value.find((opt) => opt.value === toValue(type))?.title
  );

  return {
    bookingTypeOptions,
    bookingTypeText,
  };
}
