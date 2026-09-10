import type { CalendarAppointmentEvent } from "@/components/booking/calendar/rrCalendarAppointmentEvent.vue";
import type { Ref } from "vue";

import { ref } from "vue";

export const useCalendarPopup = (dragWasPerformed: Ref<boolean>) => {
  const selectedOpen = ref<boolean>(false);
  const selectedEvent = ref<CalendarAppointmentEvent | undefined>(undefined);
  const selectedElement = ref<HTMLElement | undefined>(undefined);

  const showEvent = (nativeEvent: Event, payload: { event: unknown }) => {
    if (dragWasPerformed.value) {
      dragWasPerformed.value = false;
      nativeEvent.stopPropagation();
      return;
    }

    const event = payload.event as CalendarAppointmentEvent;
    const open = () => {
      selectedEvent.value = event;
      selectedElement.value = nativeEvent.target as HTMLElement;
      requestAnimationFrame(() => requestAnimationFrame(() => (selectedOpen.value = true)));
    };

    if (selectedOpen.value) {
      selectedOpen.value = false;
      requestAnimationFrame(() => requestAnimationFrame(() => open()));
    } else {
      open();
    }

    nativeEvent.stopPropagation();
  };

  return {
    selectedOpen,
    selectedEvent,
    selectedElement,
    showEvent,
  };
};
