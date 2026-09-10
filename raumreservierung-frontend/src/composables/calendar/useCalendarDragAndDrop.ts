import type { CalendarAppointmentEvent } from "@/components/booking/calendar/rrCalendarAppointmentEvent.vue";
import type { Ref } from "vue";
import type { CalendarDayBodySlotScope } from "vuetify/lib/components/VCalendar/types";

import { ref } from "vue";

export function useCalendarDragAndDrop(
  bookingId: Ref<string>,
  localEvents: Ref<CalendarAppointmentEvent[]>,
  onDragEnd: (event: CalendarAppointmentEvent) => void
) {
  const dragEvent = ref<CalendarAppointmentEvent | undefined>(undefined);
  const dragTime = ref<number | undefined>(undefined);
  const dragWasPerformed = ref<boolean>(false);
  const dragOriginalData = ref<
    | {
        start: number;
        end: number;
        category: string;
      }
    | undefined
  >(undefined);

  const startDrag = (
    nativeEvent: Event,
    payload: { event: unknown; timed: boolean }
  ) => {
    const payloadEvent = payload.event as CalendarAppointmentEvent;

    if (
      payloadEvent.raw.bookingMinimal.id === bookingId.value &&
      payload.timed
    ) {
      const realEvent = localEvents.value.find(
        (e) => e.raw.id === payloadEvent.raw.id
      );

      if (realEvent) {
        dragEvent.value = realEvent;
        dragTime.value = undefined;
        dragWasPerformed.value = false;

        dragOriginalData.value = {
          start: realEvent.start.getTime(),
          end: realEvent.end.getTime(),
          category: realEvent.category,
        };

        nativeEvent.preventDefault();
      }
    }
  };

  const endDrag = () => {
    if (!dragEvent.value || !dragOriginalData.value) {
      return;
    }

    if (dragWasPerformed.value) {
      onDragEnd(dragEvent.value);
    }

    dragEvent.value = undefined;
    dragTime.value = undefined;
    dragOriginalData.value = undefined;
  };

  const cancelDrag = () => {
    if (dragEvent.value && dragOriginalData.value) {
      dragEvent.value.start = new Date(dragOriginalData.value.start);
      dragEvent.value.end = new Date(dragOriginalData.value.end);
      dragEvent.value.category = dragOriginalData.value.category;
    }

    dragEvent.value = undefined;
    dragTime.value = undefined;
    dragOriginalData.value = undefined;
    dragWasPerformed.value = false;
  };

  const toTime = (tms: CalendarDayBodySlotScope): number => {
    return new Date(
      tms.year,
      tms.month - 1,
      tms.day,
      tms.hour,
      tms.minute
    ).getTime();
  };

  const roundTime = (time: number, down = true) => {
    const roundDownTime = 15 * 60 * 1000;
    return down
      ? time - (time % roundDownTime)
      : time + (roundDownTime - (time % roundDownTime));
  };

  const mouseMove = (_: Event, payload: CalendarDayBodySlotScope) => {
    if (!dragEvent.value || !dragOriginalData.value) {
      return;
    }

    const mouse = toTime(payload);

    if (dragTime.value === undefined) {
      dragTime.value = mouse - dragEvent.value.start.getTime();
    } else {
      dragWasPerformed.value = true;
    }

    const duration = dragOriginalData.value.end - dragOriginalData.value.start;
    const newStartTime = mouse - dragTime.value;
    const newStart = roundTime(newStartTime);

    dragEvent.value.start = new Date(newStart);
    dragEvent.value.end = new Date(newStart + duration);

    const targetCategory = payload.category?.categoryName;
    if (targetCategory) {
      dragEvent.value.category = targetCategory;
    }
  };

  return {
    dragEvent,
    dragTime,
    dragWasPerformed,
    dragOriginalData,
    startDrag,
    endDrag,
    cancelDrag,
    mouseMove,
  };
}
