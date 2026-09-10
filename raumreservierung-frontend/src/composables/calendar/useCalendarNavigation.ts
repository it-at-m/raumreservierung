import { computed, ref, type Ref } from "vue";
import { dateEquals } from "@/util/timeUtil.ts";

export const useCalendarNavigation = (focusDate: Ref<Date>, displayedRoomsCount: Ref<number>) => {
  const currentCalendarDate = ref<Date>(new Date(focusDate.value));

  const isDayView = computed(() => {
    return displayedRoomsCount.value > 1;
  });

  const startDate = computed(() => {
    const date = new Date(currentCalendarDate.value);
    if (isDayView.value) {
      return date;
    }

    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1);
    date.setDate(diff);

    return date;
  });

  const endDate = computed(() => {
    if (isDayView.value) {
      return startDate.value;
    }

    const end = new Date(startDate.value);
    end.setDate(end.getDate() + 9);
    return end;
  });

  const isFocusedWeek = computed(() => {
    return dateEquals(currentCalendarDate.value, focusDate.value);
  });

  const calendarTitle = computed(() => {
    return startDate.value.toLocaleDateString("de-DE", {
      month: "long",
      year: "numeric",
    });
  });

  const next = () => {
    const newDate = new Date(currentCalendarDate.value);
    newDate.setDate(newDate.getDate() + (isDayView.value ? 1 : 7));
    currentCalendarDate.value = newDate;
  };

  const prev = () => {
    const newDate = new Date(currentCalendarDate.value);
    newDate.setDate(newDate.getDate() - (isDayView.value ? 1 : 7));
    currentCalendarDate.value = newDate;
  };

  const jumpToBooking = () => {
    currentCalendarDate.value = new Date(focusDate.value);
  };

  return {
    currentCalendarDate,
    isDayView,
    startDate,
    endDate,
    isFocusedWeek,
    calendarTitle,
    next,
    prev,
    jumpToBooking,
  };
};
