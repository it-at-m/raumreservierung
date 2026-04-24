import type { HolidayResponseDTO } from "@/api/raumreservierung-backend";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

import { useGetHolidays } from "@/composables/api/useHolidayApi.ts";

export const useHolidayStore = defineStore("holiday", () => {
  const { call, loading, error, data } = useGetHolidays();

  const activeYear = ref<number>(new Date().getFullYear());
  const holidaysByYear = ref<Record<number, readonly HolidayResponseDTO[]>>({});

  const loadYear = async (year: number, force = false) => {
    activeYear.value = year;

    if (holidaysByYear.value && holidaysByYear.value[year] && !force) {
      return;
    }

    await call({ year: year });

    if (!error.value && data.value) {
      holidaysByYear.value[year] = data.value;
    }
  };

  const currentHolidays = computed(
    () => holidaysByYear.value[activeYear.value] || []
  );

  return { loading, error, currentHolidays, loadYear };
});
