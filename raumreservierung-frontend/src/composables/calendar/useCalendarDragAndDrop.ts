import type { CalendarAppointmentEvent } from "@/components/booking/calendar/rrCalendarAppointmentEvent.vue";
import type { CalendarTimestamp } from "@/types/CalendarTimestamp.ts";
import type { Ref } from "vue";

import { ref } from "vue";
import { BookingStatusDTOCurrentStatusEnum } from "@/api/raumreservierung-backend";
import { SCHEDULE_DEFAULT_DURATION } from "@/constants.ts";

export function useCalendarDragAndDrop(
  bookingId: Ref<string | undefined>,
  localEvents: Ref<CalendarAppointmentEvent[]>,
  onDragEnd: (event: CalendarAppointmentEvent) => void,
  onCreateEnd?: (event: CalendarAppointmentEvent) => void
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

  const createEvent = ref<CalendarAppointmentEvent | undefined>(undefined);
  const createStart = ref<number | undefined>(undefined);

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

  const startTime = (nativeEvent: Event, payload: CalendarTimestamp) => {
    if (bookingId.value !== undefined) {
      return;
    }

    if (createEvent.value !== undefined) {
      return;
    }

    const mouse = toTime(payload);
    createStart.value = roundTime(mouse);

    const targetCategory =
      typeof payload.category === "string"
        ? payload.category
        : payload.category?.categoryName || "";

    createEvent.value = {
      start: new Date(createStart.value),
      end: new Date(createStart.value + SCHEDULE_DEFAULT_DURATION),
      category: targetCategory,
      timed: true,
      raw: {
        id: undefined,
        bookingMinimal: {
          id: undefined,
          status: BookingStatusDTOCurrentStatusEnum.NEW,
          roomId: targetCategory,
        },
        schedule: { occupancyStart: "", occupancyEnd: "" },
      },
    } as unknown as CalendarAppointmentEvent;

    localEvents.value.push(createEvent.value);
  };

  const endDrag = () => {
    if (dragEvent.value && dragOriginalData.value && dragWasPerformed.value) {
      onDragEnd(dragEvent.value);
    }

    if (createEvent.value && onCreateEnd) {
      if (
        createEvent.value.start.getTime() !== createEvent.value.end.getTime()
      ) {
        onCreateEnd(createEvent.value);
      } else {
        localEvents.value = localEvents.value.filter(
          (e) => e.raw.id !== undefined
        );
      }
    }

    dragEvent.value = undefined;
    dragTime.value = undefined;
    dragOriginalData.value = undefined;

    createEvent.value = undefined;
    createStart.value = undefined;
  };

  const cancelDrag = () => {
    if (dragEvent.value && dragOriginalData.value) {
      dragEvent.value.start = new Date(dragOriginalData.value.start);
      dragEvent.value.end = new Date(dragOriginalData.value.end);
      dragEvent.value.category = dragOriginalData.value.category;
    }

    if (createEvent.value) {
      localEvents.value = localEvents.value.filter(
        (e) => e.raw.id !== undefined
      );
    }

    dragEvent.value = undefined;
    dragTime.value = undefined;
    dragOriginalData.value = undefined;
    dragWasPerformed.value = false;

    createEvent.value = undefined;
    createStart.value = undefined;
  };

  const toTime = (tms: CalendarTimestamp): number => {
    return new Date(
      tms.year,
      tms.month - 1,
      tms.day,
      tms.hour,
      tms.minute
    ).getTime();
  };

  const roundTime = (time: number, down = true) => {
    return down
      ? time - (time % SCHEDULE_DEFAULT_DURATION)
      : time + (SCHEDULE_DEFAULT_DURATION - (time % SCHEDULE_DEFAULT_DURATION));
  };

  const mouseMove = (_: Event, payload: CalendarTimestamp) => {
    const mouse = toTime(payload);

    if (dragEvent.value && dragOriginalData.value) {
      if (dragTime.value === undefined) {
        dragTime.value = mouse - dragEvent.value.start.getTime();
      } else {
        dragWasPerformed.value = true;
      }

      const duration =
        dragOriginalData.value.end - dragOriginalData.value.start;
      const newStartTime = mouse - dragTime.value;
      const newStart = roundTime(newStartTime);

      dragEvent.value.start = new Date(newStart);
      dragEvent.value.end = new Date(newStart + duration);

      const targetCategory =
        typeof payload.category === "string"
          ? payload.category
          : payload.category?.categoryName;

      if (targetCategory) {
        dragEvent.value.category = targetCategory;
      }
    } else if (createEvent.value && createStart.value !== undefined) {
      const mouseRounded = roundTime(mouse, false);
      const min = Math.min(mouseRounded, createStart.value);
      const max = Math.max(mouseRounded, createStart.value);

      createEvent.value.start = new Date(min);
      createEvent.value.end = new Date(max);
    }
  };

  return {
    dragEvent,
    dragTime,
    dragWasPerformed,
    dragOriginalData,
    startDrag,
    startTime,
    endDrag,
    cancelDrag,
    mouseMove,
  };
}
