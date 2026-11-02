import { useI18n } from "vue-i18n";
import type { WeekScheduleAsPDFCategoryEnum } from "@/api/raumreservierung-backend";

export function weekScheduleCategory() {
  const { t } = useI18n();

  const weekScheduleCategoryMap: Record<WeekScheduleAsPDFCategoryEnum, string> =
    {
      ALTES_RATHAUS: t("domain.weekScheduleCategory.altesRathaus"),
      NEUES_RATHAUS: t("domain.weekScheduleCategory.neuesRathaus"),
    };

  return { weekScheduleCategoryMap };
}
