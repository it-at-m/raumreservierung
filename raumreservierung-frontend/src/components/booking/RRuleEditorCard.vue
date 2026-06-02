<template>
  <card-form
    subtitle="Serienmuster"
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
                  <span class="mr-2"> Jeden / Alle </span>
                  <v-number-input
                    v-model="dailyInterval"
                    density="compact"
                    variant="outlined"
                    color="accent"
                    max-width="70px"
                    hide-details
                    control-variant="hidden"
                    :min="1"
                    :max="99"
                  />
                  <span class="ml-2">Tag(e)</span>
                </template>
              </v-radio>
              <v-radio
                value="workdays"
                label="Jeden Arbeitstag"
              />
            </v-radio-group>
          </template>

          <template v-if="frequency === 'weekly'">
            <div class="d-flex align-center mb-4">
              <span class="mr-2"> Jeden / Alle </span>
              <v-number-input
                v-model="weeklyInterval"
                :disabled="disabled"
                density="compact"
                variant="outlined"
                color="accent"
                max-width="70px"
                hide-details
                control-variant="hidden"
                :min="1"
                :max="99"
              />
              <span class="ml-2">Woche(n) an folgenden Tagen:</span>
            </div>

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
              color="accent"
            >
              <v-radio value="specific_day">
                <template #label>
                  <span class="mr-2"> Am </span>
                  <v-number-input
                    v-model="monthlyDay"
                    :disabled="disabled"
                    density="compact"
                    variant="outlined"
                    color="accent"
                    max-width="70px"
                    hide-details
                    control-variant="hidden"
                    :min="1"
                    :max="31"
                    suffix="."
                  />
                  <span class="mx-2"> Tag, alle </span>
                  <v-number-input
                    v-model="monthlyIntervalOption1"
                    density="compact"
                    :disabled="disabled"
                    variant="outlined"
                    color="accent"
                    max-width="70px"
                    hide-details
                    control-variant="hidden"
                    :min="1"
                    :max="99"
                  />
                  <span class="ml-2"> Monat(e) </span>
                </template>
              </v-radio>

              <v-radio
                value="relative_day"
                class="mt-2"
              >
                <template #label>
                  <span class="mr-2"> Am </span>
                  <v-select
                    v-model="monthlyRelativePosition"
                    :items="positionOptions"
                    :disabled="disabled"
                    density="compact"
                    variant="outlined"
                    hide-details
                    color="accent"
                    max-width="120px"
                    class="mr-2"
                  />
                  <v-select
                    v-model="monthlyRelativeDay"
                    :items="relativeWeekdayOptions"
                    :disabled="disabled"
                    density="compact"
                    variant="outlined"
                    hide-details
                    color="accent"
                    max-width="150px"
                    class="mr-2"
                  />
                  <span class="mr-2"> jeden </span>
                  <v-number-input
                    v-model="monthlyIntervalOption2"
                    density="compact"
                    variant="outlined"
                    :disabled="disabled"
                    color="accent"
                    max-width="70px"
                    hide-details
                    control-variant="hidden"
                    :min="1"
                    :max="99"
                  />
                  <span class="ml-2"> Monat(e) </span>
                </template>
              </v-radio>
            </v-radio-group>
          </template>
        </v-col>
      </v-row>
    </template>
  </card-form>
  <card-form subtitle="Seriendauer">
    <template #text>
      <v-radio-group
        v-model="endOption"
        :disabled="disabled"
        color="accent"
        hide-details
      >
        <v-radio value="count">
          <template #label>
            <span class="mr-2"> Endet nach </span>
            <v-number-input
              v-model="endCount"
              :disabled="disabled || endOption !== 'count'"
              density="compact"
              variant="outlined"
              color="accent"
              max-width="70px"
              hide-details
              control-variant="hidden"
              :min="1"
              :max="52"
            />
            <span class="ml-2"> Termin(en) </span>
          </template>
        </v-radio>

        <v-radio
          value="until"
          class="mt-2"
        >
          <template #label>
            <span class="mr-2"> Endet am </span>
            <date-time-text-field
              v-model="endDate"
              :disabled="disabled || endOption !== 'until'"
              color="accent"
              type="date"
              hide-details
              class="ml-2"
              style="max-width: 150px"
            />
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
import { useDisplay } from "vuetify/framework";

import CardForm from "@/components/common/CardForm.vue";
import DateTimeTextField from "@/components/common/date/DateTimeTextField.vue";

interface SelectOption<T> {
  value: T;
  title: string;
}

const { modelValue, disabled = false } = defineProps<{
  modelValue?: string;
  disabled?: boolean;
}>();
// TODO Seriendauer noch hinzufügen

const { mdAndUp } = useDisplay();

// --- STATE-FLAGS (Semaphore Pattern) ---
const isInternalChange = ref(false); // Verhindert, dass wir unsere eigene String-Generierung wieder parsen
const isParsing = ref(false); // Verhindert, dass das Parsen von außen einen neuen String generiert

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

// --- UI STATE: SERIENDAUER ---
type EndOption = "count" | "until";
const endOption = ref<EndOption>("count");
const endCount = ref<number>(10);
const endDate = ref<Date>(new Date());

// --- HILFSMAPPING ---
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

// --- LOGIK ---
// Hilfsfunktion für Typescript: RRule kann Number, String oder Weekday-Objekt liefern
const extractWeekdayNumber = (day: any): number => {
  if (typeof day === "number") return day;
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
  // Wenn es ein Objekt ist, nehmen wir das weekday Property
  return day.weekday ?? 0;
};

const generateRRule = () => {
  const options: Partial<Options> = {}; // TS Fix: Unexpected any entfernt

  if (frequency.value === "daily") {
    if (dailyOption.value === "workdays") {
      options.freq = RRule.WEEKLY;
      options.byweekday = [RRule.MO, RRule.TU, RRule.WE, RRule.TH, RRule.FR];
      options.interval = 1;
    } else {
      options.freq = RRule.DAILY;
      options.interval = dailyInterval.value;
    }
  } else if (frequency.value === "weekly") {
    options.freq = RRule.WEEKLY;
    options.interval = weeklyInterval.value;
    options.byweekday = weeklyDays.value.map((d) => rruleWeekdayMap[d]);
  } else if (frequency.value === "monthly") {
    options.freq = RRule.MONTHLY;

    if (monthlyOption.value === "specific_day") {
      options.bymonthday = [monthlyDay.value];
      options.interval = monthlyIntervalOption1.value;
    } else {
      options.interval = monthlyIntervalOption2.value;
      options.bysetpos = [parseInt(monthlyRelativePosition.value)];

      if (monthlyRelativeDay.value === "DAY") {
        options.byweekday = [
          RRule.MO,
          RRule.TU,
          RRule.WE,
          RRule.TH,
          RRule.FR,
          RRule.SA,
          RRule.SU,
        ];
      } else if (monthlyRelativeDay.value === "WORKDAY") {
        options.byweekday = [RRule.MO, RRule.TU, RRule.WE, RRule.TH, RRule.FR];
      } else if (monthlyRelativeDay.value === "WEEKENDDAY") {
        options.byweekday = [RRule.SA, RRule.SU];
      } else {
        options.byweekday = [
          rruleWeekdayMap[monthlyRelativeDay.value as DayType],
        ];
      }
    }
  }

  // --- SERIENDAUER LOGIK ANHÄNGEN ---
  if (endOption.value === "count") {
    if (!endCount.value || endCount.value < 1) {
      endCount.value = 1;
    }

    options.count = endCount.value;
  } else if (endOption.value === "until" && endDate.value) {
    options.until = endDate.value;
  }

  try {
    const rule = new RRule(options);
    isInternalChange.value = true;
    emit("update:modelValue", rule.toString());
  } catch (e) {
    console.error("Fehler beim Generieren der RRule:", e);
  }
};

const parseIncomingRRule = (rruleString: string) => {
  if (!rruleString) return;

  try {
    isParsing.value = true; // Sperrt die Generierung, während wir die Refs updaten

    const cleanString = rruleString.replace(/^RRULE:/i, "");
    const rule = RRule.fromString(cleanString);
    const orig = rule.origOptions;
    const freq = orig.freq;

    if (freq === RRule.DAILY) {
      frequency.value = "daily";
      dailyOption.value = "every";
      dailyInterval.value = orig.interval || 1;
    } else if (freq === RRule.WEEKLY) {
      const byweekdayArray = orig.byweekday
        ? Array.isArray(orig.byweekday)
          ? orig.byweekday
          : [orig.byweekday]
        : [];
      // TS Fix: Type ByWeekday | number
      const weekdayNums = byweekdayArray.map(extractWeekdayNumber).sort();

      const isWorkdays =
        weekdayNums.length === 5 && weekdayNums.every((v, i) => v === i);

      if (isWorkdays && (orig.interval === 1 || !orig.interval)) {
        frequency.value = "daily";
        dailyOption.value = "workdays";
      } else {
        frequency.value = "weekly";
        weeklyInterval.value = orig.interval || 1;
        weeklyDays.value = weekdayNums
          .map((num) => numToDayStr[num])
          .filter(Boolean) as DayType[];
      }
    } else if (freq === RRule.MONTHLY) {
      frequency.value = "monthly";

      if (orig.bysetpos !== undefined || orig.byweekday !== undefined) {
        monthlyOption.value = "relative_day";
        monthlyIntervalOption2.value = orig.interval || 1;

        if (orig.bysetpos) {
          const pos = Array.isArray(orig.bysetpos)
            ? orig.bysetpos[0]
            : orig.bysetpos;
          monthlyRelativePosition.value = String(pos);
        }

        if (orig.byweekday) {
          const byweekdayArray = Array.isArray(orig.byweekday)
            ? orig.byweekday
            : [orig.byweekday];
          const weekdayNums = byweekdayArray.map(extractWeekdayNumber).sort();

          if (weekdayNums.length === 7) {
            monthlyRelativeDay.value = "DAY";
          } else if (
            weekdayNums.length === 5 &&
            weekdayNums.every((v, i) => v === i)
          ) {
            monthlyRelativeDay.value = "WORKDAY";
          } else if (
            weekdayNums.length === 2 &&
            weekdayNums[0] === 5 &&
            weekdayNums[1] === 6
          ) {
            monthlyRelativeDay.value = "WEEKENDDAY";
          } else if (weekdayNums.length === 1) {
            monthlyRelativeDay.value = numToDayStr[weekdayNums[0]] || "MO";
          }
        }
      } else {
        monthlyOption.value = "specific_day";
        monthlyIntervalOption1.value = orig.interval || 1;
        if (orig.bymonthday) {
          const mday = Array.isArray(orig.bymonthday)
            ? orig.bymonthday[0]
            : orig.bymonthday;
          monthlyDay.value = mday || 1;
        }
      }
    }

    // --- SERIENDAUER PARSEN ---
    if (orig.count !== undefined && orig.count !== null) {
      endOption.value = "count";
      endCount.value = orig.count;
    } else if (orig.until !== undefined && orig.until !== null) {
      endOption.value = "until";
      // orig.until ist bereits ein geparstes Date-Objekt von der rrule Library!
      endDate.value = orig.until;
    } else {
      // Fallback, falls der String unendlich ist (wird durch deine UI quasi nicht generiert)
      endOption.value = "count";
      endCount.value = 10;
    }
  } catch (e) {
    console.error("Fehler beim Parsen der RRule:", e);
  } finally {
    nextTick(() => {
      isParsing.value = false;
    });
  }
};

// --- WATCHER ---

// Reagiert auf alle internen UI-Änderungen
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
    // Wenn wir gerade von außen Daten einladen, nichts tun!
    if (isParsing.value) {
      return;
    }

    console.log("internal Change");

    generateRRule();
  },
  { deep: true }
);

// Reagiert auf Änderungen von außen am modelValue
watch(
  () => modelValue,
  (newValue) => {
    if (isInternalChange.value) {
      isInternalChange.value = false;
      return;
    }
    console.log("external Change");
    parseIncomingRRule(newValue || "");
  },
  { immediate: true }
);

onMounted(() => {
  if (!modelValue) {
    generateRRule();
  }
});
</script>

<style scoped></style>
