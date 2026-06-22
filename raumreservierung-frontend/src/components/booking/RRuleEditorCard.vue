<template>
  <card-form
    :subtitle="t('components.rruleEditorCard.seriesPattern')"
    class="mb-4"
  >
    <template #text>
      <v-row>
        <v-col
          cols="12"
          md="3"
          class="pb-2"
          :class="{ 'border-e-sm': mdAndUp, 'border-b-sm': !mdAndUp }"
        >
          <v-radio-group
            v-model="frequency"
            color="accent"
            :disabled="disabled"
            hide-details
          >
            <v-radio
              v-for="freq in frequencyOptions"
              :key="freq.value"
              :label="freq.title"
              :value="freq.value"
            />
          </v-radio-group>
        </v-col>

        <v-col
          cols="12"
          md="9"
        >
          <template v-if="frequency === 'daily'">
            <v-radio-group
              v-model="dailyOption"
              :disabled="disabled"
              color="accent"
            >
              <v-radio value="every">
                <template #label>
                  <i18n-t
                    keypath="components.rruleEditorCard.daily.every"
                    tag="span"
                    :plural="dailyInterval"
                    class="d-flex align-center"
                  >
                    <template #input>
                      <v-number-input
                        v-model="dailyInterval"
                        :disabled="disabled || dailyOption !== 'every'"
                        density="compact"
                        variant="outlined"
                        color="accent"
                        max-width="80px"
                        hide-details
                        control-variant="hidden"
                        :min="1"
                        :max="99"
                        class="mx-2"
                      />
                    </template>
                  </i18n-t>
                </template>
              </v-radio>
              <v-radio
                value="workdays"
                :label="t('components.rruleEditorCard.daily.workdays')"
              />
            </v-radio-group>
          </template>

          <template v-if="frequency === 'weekly'">
            <i18n-t
              keypath="components.rruleEditorCard.weekly.every"
              tag="div"
              :plural="weeklyInterval"
              class="d-flex align-center mb-4"
            >
              <template #input>
                <v-number-input
                  v-model="weeklyInterval"
                  :disabled="disabled"
                  density="compact"
                  variant="outlined"
                  color="accent"
                  max-width="80px"
                  hide-details
                  control-variant="hidden"
                  :min="1"
                  :max="99"
                  class="mx-2"
                />
              </template>
            </i18n-t>

            <v-row gap="0">
              <v-col
                v-for="weekday in weekdays"
                :key="weekday.value"
                cols="12"
                sm="4"
                md="3"
              >
                <v-checkbox
                  v-model="weeklyDays"
                  :disabled="disabled"
                  :value="weekday.value"
                  density="compact"
                  :label="weekday.title"
                  hide-details
                  color="accent"
                />
              </v-col>
            </v-row>
          </template>

          <template v-if="frequency === 'monthly'">
            <v-radio-group
              v-model="monthlyOption"
              :disabled="disabled"
              color="accent"
            >
              <v-radio value="specific_day">
                <template #label>
                  <i18n-t
                    keypath="components.rruleEditorCard.monthly.specificDay"
                    tag="span"
                    :plural="monthlyIntervalOption1"
                    class="d-flex align-center w-100"
                  >
                    <template #day>
                      <v-number-input
                        v-model="monthlyDay"
                        :disabled="disabled || monthlyOption !== 'specific_day'"
                        density="compact"
                        variant="outlined"
                        color="accent"
                        max-width="80px"
                        hide-details
                        control-variant="hidden"
                        :min="1"
                        :max="31"
                        class="mx-2"
                      />
                    </template>
                    <template #interval>
                      <v-number-input
                        v-model="monthlyIntervalOption1"
                        :disabled="disabled || monthlyOption !== 'specific_day'"
                        density="compact"
                        variant="outlined"
                        color="accent"
                        max-width="80px"
                        hide-details
                        control-variant="hidden"
                        :min="1"
                        :max="99"
                        class="mx-2"
                      />
                    </template>
                  </i18n-t>
                </template>
              </v-radio>

              <v-radio
                value="relative_day"
                class="mt-2"
              >
                <template #label>
                  <i18n-t
                    keypath="components.rruleEditorCard.monthly.relativeDay"
                    tag="span"
                    :plural="monthlyIntervalOption2"
                    class="d-flex align-center flex-wrap w-100"
                  >
                    <template #position>
                      <v-select
                        v-model="monthlyRelativePosition"
                        :items="positionOptions"
                        :disabled="disabled || monthlyOption !== 'relative_day'"
                        density="compact"
                        variant="outlined"
                        hide-details
                        color="accent"
                        max-width="120px"
                        class="mx-2"
                      />
                    </template>
                    <template #day>
                      <v-select
                        v-model="monthlyRelativeDay"
                        :items="relativeWeekdayOptions"
                        :disabled="disabled || monthlyOption !== 'relative_day'"
                        density="compact"
                        variant="outlined"
                        hide-details
                        color="accent"
                        max-width="150px"
                        class="mx-2"
                      />
                    </template>
                    <template #interval>
                      <v-number-input
                        v-model="monthlyIntervalOption2"
                        :disabled="disabled || monthlyOption !== 'relative_day'"
                        density="compact"
                        variant="outlined"
                        color="accent"
                        max-width="80px"
                        hide-details
                        control-variant="hidden"
                        :min="1"
                        :max="99"
                        class="mx-2"
                      />
                    </template>
                  </i18n-t>
                </template>
              </v-radio>
            </v-radio-group>
          </template>
        </v-col>
      </v-row>
    </template>
  </card-form>

  <card-form :subtitle="t('components.rruleEditorCard.seriesDuration')">
    <template #text>
      <v-radio-group
        v-model="endOption"
        :disabled="disabled"
        color="accent"
        hide-details
      >
        <v-radio value="count">
          <template #label>
            <i18n-t
              keypath="components.rruleEditorCard.end.afterCount"
              tag="span"
              :plural="endCount"
              class="d-flex align-center"
            >
              <template #count>
                <v-number-input
                  v-model="endCount"
                  :disabled="disabled || endOption !== 'count'"
                  density="compact"
                  variant="outlined"
                  color="accent"
                  max-width="80px"
                  hide-details
                  control-variant="hidden"
                  :min="1"
                  :max="52"
                  class="mx-2"
                />
              </template>
            </i18n-t>
          </template>
        </v-radio>

        <v-radio
          value="until"
          class="mt-2"
        >
          <template #label>
            <i18n-t
              keypath="components.rruleEditorCard.end.onDate"
              tag="span"
              class="d-flex align-center"
            >
              <template #date>
                <date-time-text-field
                  v-model="endDate"
                  :disabled="disabled || endOption !== 'until'"
                  color="accent"
                  type="date"
                  hide-details
                  class="ml-2"
                />
              </template>
            </i18n-t>
          </template>
        </v-radio>
      </v-radio-group>
    </template>
  </card-form>
</template>
<script setup lang="ts">
import type { Options } from "rrule";

import { RRule, Weekday } from "rrule";
import { nextTick, onMounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useDisplay } from "vuetify/framework";

import CardForm from "@/components/common/CardForm.vue";
import DateTimeTextField from "@/components/common/date/DateTimeTextField.vue";

const { t } = useI18n();

interface SelectOption<T> {
  value: T;
  title: string;
}

const { modelValue, disabled = false } = defineProps<{
  modelValue?: string;
  disabled?: boolean;
}>();

const { mdAndUp } = useDisplay();

// --- STATE-FLAGS ---
const isInternalChange = ref(false);
const isParsing = ref(false);

// --- UI STATE ---
type FrequencyType = "daily" | "weekly" | "monthly";
const frequency = ref<FrequencyType>("weekly");
const frequencyOptions: SelectOption<FrequencyType>[] = [
  { title: "Täglich", value: "daily" },
  { title: "Wöchentlich", value: "weekly" },
  { title: "Monatlich", value: "monthly" },
];

type DailyOption = "every" | "workdays";
const dailyOption = ref<DailyOption>("every");
const dailyInterval = ref<number>(1);

type DayType = "MO" | "TU" | "WE" | "TH" | "FR" | "SA" | "SU";
const weeklyInterval = ref(1);
const weeklyDays = ref<DayType[]>(["MO"]);
const weekdays: SelectOption<DayType>[] = [
  { title: "Montag", value: "MO" },
  { title: "Dienstag", value: "TU" },
  { title: "Mittwoch", value: "WE" },
  { title: "Donnerstag", value: "TH" },
  { title: "Freitag", value: "FR" },
  { title: "Samstag", value: "SA" },
  { title: "Sonntag", value: "SU" },
];

type MonthlyOption = "specific_day" | "relative_day";
const monthlyOption = ref<MonthlyOption>("specific_day");
const monthlyDay = ref(1);
const monthlyIntervalOption1 = ref(1);
const monthlyIntervalOption2 = ref(1);

const monthlyRelativePosition = ref("1");
const positionOptions = [
  { title: "Ersten", value: "1" },
  { title: "Zweiten", value: "2" },
  { title: "Dritten", value: "3" },
  { title: "Vierten", value: "4" },
  { title: "Letzten", value: "-1" },
];

const monthlyRelativeDay = ref("MO");
const relativeWeekdayOptions = [
  { title: "Tag", value: "DAY" },
  { title: "Arbeitstag", value: "WORKDAY" },
  { title: "Wochenendtag", value: "WEEKENDDAY" },
  ...weekdays,
];

type EndOption = "count" | "until";
const endOption = ref<EndOption>("count");
const endCount = ref<number>(10);
const endDate = ref<Date>(new Date());

// --- Mapping ---
const rruleWeekdayMap: Record<DayType, Weekday> = {
  MO: RRule.MO,
  TU: RRule.TU,
  WE: RRule.WE,
  TH: RRule.TH,
  FR: RRule.FR,
  SA: RRule.SA,
  SU: RRule.SU,
};

const numToDayStr: Record<number, DayType> = {
  0: "MO",
  1: "TU",
  2: "WE",
  3: "TH",
  4: "FR",
  5: "SA",
  6: "SU",
};

const emit = defineEmits<{
  "update:modelValue": [value: string];
}>();

// --- Logic ---
const extractWeekdayNumber = (day: unknown): number => {
  if (typeof day === "number") {
    return day;
  }
  if (typeof day === "string") {
    const strMap: Record<string, number> = {
      MO: 0,
      TU: 1,
      WE: 2,
      TH: 3,
      FR: 4,
      SA: 5,
      SU: 6,
    };
    return strMap[day] ?? 0;
  }
  if (day && typeof day === "object" && "weekday" in day) {
    return (day as { weekday: number }).weekday ?? 0;
  }
  return 0;
};

const generateRRule = () => {
  const options: Partial<Options> = {};

  switch (frequency.value) {
    case "daily":
      if (dailyOption.value === "workdays") {
        options.freq = RRule.WEEKLY;
        options.byweekday = [RRule.MO, RRule.TU, RRule.WE, RRule.TH, RRule.FR];
        options.interval = 1;
      } else {
        options.freq = RRule.DAILY;
        options.interval = dailyInterval.value;
      }
      break;

    case "weekly":
      options.freq = RRule.WEEKLY;
      options.interval = weeklyInterval.value;
      options.byweekday = weeklyDays.value.map((d) => rruleWeekdayMap[d]);
      break;

    case "monthly":
      options.freq = RRule.MONTHLY;
      if (monthlyOption.value === "specific_day") {
        options.bymonthday = [monthlyDay.value];
        options.interval = monthlyIntervalOption1.value;
      } else {
        options.interval = monthlyIntervalOption2.value;
        options.bysetpos = [parseInt(monthlyRelativePosition.value)];

        switch (monthlyRelativeDay.value) {
          case "DAY":
            options.byweekday = [
              RRule.MO,
              RRule.TU,
              RRule.WE,
              RRule.TH,
              RRule.FR,
              RRule.SA,
              RRule.SU,
            ];
            break;
          case "WORKDAY":
            options.byweekday = [
              RRule.MO,
              RRule.TU,
              RRule.WE,
              RRule.TH,
              RRule.FR,
            ];
            break;
          case "WEEKENDDAY":
            options.byweekday = [RRule.SA, RRule.SU];
            break;
          default:
            options.byweekday = [
              rruleWeekdayMap[monthlyRelativeDay.value as DayType],
            ];
        }
      }
      break;
  }

  switch (endOption.value) {
    case "count":
      endCount.value =
        !endCount.value || endCount.value < 1 ? 1 : endCount.value;
      options.count = endCount.value;
      break;
    case "until":
      if (endDate.value) options.until = endDate.value;
      break;
  }

  const rule = new RRule(options);
  isInternalChange.value = true;
  emit("update:modelValue", rule.toString());
};

const parseIncomingRRule = (rruleString: string) => {
  if (!rruleString) return;

  try {
    isParsing.value = true;

    const cleanString = rruleString.replace(/^RRULE:/i, "");
    const rule = RRule.fromString(cleanString);
    const orig = rule.origOptions;

    switch (orig.freq) {
      case RRule.DAILY: {
        frequency.value = "daily";
        dailyOption.value = "every";
        dailyInterval.value = orig.interval || 1;
        break;
      }

      case RRule.WEEKLY: {
        const byweekdayArray = orig.byweekday
          ? Array.isArray(orig.byweekday)
            ? orig.byweekday
            : [orig.byweekday]
          : [];
        const weekdayNums = byweekdayArray.map(extractWeekdayNumber).sort();
        const isWorkdays =
          weekdayNums.length === 5 && weekdayNums.every((v, i) => v === i);

        frequency.value =
          isWorkdays && (!orig.interval || orig.interval === 1)
            ? "daily"
            : "weekly";

        if (frequency.value === "daily") {
          dailyOption.value = "workdays";
        } else {
          weeklyInterval.value = orig.interval || 1;
          weeklyDays.value = weekdayNums
            .map((num) => numToDayStr[num])
            .filter(Boolean) as DayType[];
        }
        break;
      }

      case RRule.MONTHLY: {
        frequency.value = "monthly";

        if (orig.bysetpos !== undefined || orig.byweekday !== undefined) {
          monthlyOption.value = "relative_day";
          monthlyIntervalOption2.value = orig.interval || 1;
          monthlyRelativePosition.value = orig.bysetpos
            ? String(
                Array.isArray(orig.bysetpos) ? orig.bysetpos[0] : orig.bysetpos
              )
            : "1";

          if (orig.byweekday) {
            const bwArray = Array.isArray(orig.byweekday)
              ? orig.byweekday
              : [orig.byweekday];
            const wNums = bwArray.map(extractWeekdayNumber).sort();

            if (wNums.length === 7) monthlyRelativeDay.value = "DAY";
            else if (wNums.length === 5 && wNums.every((v, i) => v === i))
              monthlyRelativeDay.value = "WORKDAY";
            else if (wNums.length === 2 && wNums[0] === 5 && wNums[1] === 6)
              monthlyRelativeDay.value = "WEEKENDDAY";
            else if (wNums.length === 1)
              monthlyRelativeDay.value =
                numToDayStr[wNums[0] as number] || "MO";
          }
        } else {
          monthlyOption.value = "specific_day";
          monthlyIntervalOption1.value = orig.interval || 1;
          const parsedMonthDay = Array.isArray(orig.bymonthday)
            ? orig.bymonthday[0]
            : orig.bymonthday;

          monthlyDay.value = parsedMonthDay ?? 1;
        }
        break;
      }
    }

    if (orig.count !== undefined && orig.count !== null) {
      endOption.value = "count";
      endCount.value = orig.count;
    } else if (orig.until !== undefined && orig.until !== null) {
      endOption.value = "until";
      endDate.value = orig.until;
    } else {
      endOption.value = "count";
      endCount.value = 10;
    }
  } finally {
    nextTick(() => {
      isParsing.value = false;
    });
  }
};

// --- WATCHER ---
watch(
  [
    frequency,
    dailyOption,
    dailyInterval,
    weeklyInterval,
    weeklyDays,
    monthlyOption,
    monthlyDay,
    monthlyIntervalOption1,
    monthlyIntervalOption2,
    monthlyRelativePosition,
    monthlyRelativeDay,
    endOption,
    endCount,
    endDate,
  ],
  () => {
    if (isParsing.value) return;
    generateRRule();
  },
  { deep: true }
);

watch(
  () => modelValue,
  (newValue) => {
    if (isInternalChange.value) {
      isInternalChange.value = false;
      return;
    }
    parseIncomingRRule(newValue || "");
  },
  { immediate: true }
);

onMounted(() => {
  if (!modelValue) generateRRule();
});
</script>

<style scoped></style>
