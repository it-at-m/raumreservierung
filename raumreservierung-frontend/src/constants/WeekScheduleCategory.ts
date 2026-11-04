import { WeekScheduleAsPDFCategoryEnum } from "@/api/raumreservierung-backend";
import i18n from "@/plugins/i18n.ts";

const { t } = i18n.global;

export interface CategorySelectItem {
  value: WeekScheduleAsPDFCategoryEnum;
  title: string;
}

export const weekScheduleCategoryMap: Record<
  WeekScheduleAsPDFCategoryEnum,
  string
> = {
  ALTES_RATHAUS: t("domain.weekScheduleCategory.altesRathaus"),
  NEUES_RATHAUS: t("domain.weekScheduleCategory.neuesRathaus"),
};

export const weekScheduleCategoryItems: CategorySelectItem[] = (
  Object.keys(weekScheduleCategoryMap) as WeekScheduleAsPDFCategoryEnum[]
).map((value) => ({ value, title: weekScheduleCategoryMap[value] }));
