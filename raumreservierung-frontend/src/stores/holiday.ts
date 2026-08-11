import { defineStore } from "pinia";
import { computed, ref } from "vue";

import { useGetHolidays } from "@/composables/api/useHolidayApi.ts";

export const useHolidayStore = defineStore("holiday", () => {
  const activeYear = ref<number>(new Date().getFullYear());

  const params = computed(() => ({ year: activeYear.value }));
  const { data, isPending, error } = useGetHolidays(params);

  const currentHolidays = computed(() => data.value ?? []);

  const loadYear = (year: number) => {
    activeYear.value = year;
  };

  return { loading: isPending, error, currentHolidays, loadYear };
});
