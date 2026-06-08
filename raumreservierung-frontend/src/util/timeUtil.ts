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
