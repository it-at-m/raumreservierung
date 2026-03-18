import { useDateFormat } from "@vueuse/core";

export function useFormatDate(date: Date) {
  return useDateFormat(date, "DD.MM.YY");
}
