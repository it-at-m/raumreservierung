import type { WeekScheduleAsPDFCategoryEnum } from "@/api/raumreservierung-backend";

export const weekScheduleCategoryMap: Record<
  WeekScheduleAsPDFCategoryEnum,
  string
> = {
  ALTES_RATHAUS: "domain.weekScheduleCategory.altesRathaus",
  NEUES_RATHAUS: "domain.weekScheduleCategory.neuesRathaus",
};
