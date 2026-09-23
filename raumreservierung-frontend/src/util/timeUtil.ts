export const dateEquals = (
  date: Date | undefined,
  otherDate: Date | undefined
) =>
  !date || !otherDate
    ? date === otherDate
    : date.getDate() === otherDate.getDate() &&
      date.getMonth() === otherDate.getMonth() &&
      date.getFullYear() === otherDate.getFullYear();

export const timeEquals = (
  date: Date | undefined,
  otherDate: Date | undefined
) =>
  !date || !otherDate
    ? date === otherDate
    : date.getMinutes() === otherDate.getMinutes() &&
      date.getHours() === otherDate.getHours();

export const toApiDate = <T extends Date | undefined>(date: T): T =>
  !date
    ? date
    : (new Date(date.getTime() - date.getTimezoneOffset() * 60000) as T);

/**
 * Transforms given date to same date with time set to 00:00:00
 */
export const toStartOfDay = (date: Date | string): number =>
  new Date(date).setHours(0, 0, 0, 0);

/**
 * Compares two date ranges while ignoring time via toStartOfDay
 */
export const dateRangesOverlap = (
  aStart: Date,
  aEnd: Date | undefined,
  bStart: Date,
  bEnd: Date | undefined
): boolean => {
  const safeAEnd = aEnd ?? aStart;
  const safeBEnd = bEnd ?? bStart;

  return (
    toStartOfDay(aStart) <= toStartOfDay(safeBEnd) &&
    toStartOfDay(bStart) <= toStartOfDay(safeAEnd)
  );
};

export const formatDateShort = (date: Date | string | number): string =>
  new Date(date).toLocaleDateString("de-DE", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
