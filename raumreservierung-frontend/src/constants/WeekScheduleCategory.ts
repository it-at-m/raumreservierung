import { WeekScheduleAsPDFCategoryEnum } from "@/api/raumreservierung-backend";
import i18n from "@/plugins/i18n.ts";

const { t } = i18n.global;

export const weekScheduleCategories: Array<{
  value: WeekScheduleAsPDFCategoryEnum;
  title: string;
}> = [
  {
    value: WeekScheduleAsPDFCategoryEnum.ALTES_RATHAUS,
    title: t("domain.weekScheduleCategory.altesRathaus"),
  },
  {
    value: WeekScheduleAsPDFCategoryEnum.NEUES_RATHAUS,
    title: t("domain.weekScheduleCategory.neuesRathaus"),
  },
];
