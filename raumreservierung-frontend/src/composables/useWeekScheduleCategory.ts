import { computed } from "vue";
import { useI18n } from "vue-i18n";
import type { WeekScheduleAsPDFCategoryEnum } from "@/api/raumreservierung-backend";

export function useWeekScheduleCategory() {
  const { t } = useI18n();

  const weekScheduleCategoryMap = computed<
    Record<WeekScheduleAsPDFCategoryEnum, string>
  >(() => ({
    ALTES_RATHAUS: t("domain.weekScheduleCategory.altesRathaus"),
    NEUES_RATHAUS: t("domain.weekScheduleCategory.neuesRathaus"),
  }));

  return { weekScheduleCategoryMap };
}
