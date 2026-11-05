import type { WeekScheduleAsPDFCategoryEnum } from "@/api/raumreservierung-backend";
import i18n from "@/plugins/i18n.ts";
import type { Composer } from "vue-i18n";

const { t } = i18n.global as unknown as Composer;

export const weekScheduleCategoryMap: Record<
  WeekScheduleAsPDFCategoryEnum,
  string
> = {
  ALTES_RATHAUS: t("domain.weekScheduleCategory.altesRathaus"),
  NEUES_RATHAUS: t("domain.weekScheduleCategory.neuesRathaus"),
};
