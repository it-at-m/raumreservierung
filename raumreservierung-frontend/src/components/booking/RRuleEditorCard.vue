<template>
  <card-form subtitle="Serie bearbeiten">
    <template #text>
      <v-row>
        <v-col
          cols="12"
          md="3"
          class="border-e-sm"
        >
          <v-radio-group
            v-model="frequency"
            color="accent"
            hide-details
            @update:model-value="generateRRule"
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
              color="accent"
              @update:model-value="generateRRule"
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
                    @update:model-value="generateRRule"
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
                density="compact"
                variant="outlined"
                color="accent"
                max-width="70px"
                hide-details
                control-variant="hidden"
                :min="1"
                :max="99"
                @update:model-value="generateRRule"
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
                  :value="weekday.value"
                  density="compact"
                  :label="weekday.title"
                  hide-details
                  color="accent"
                  @update:model-value="generateRRule"
                />
              </v-col>
            </v-row>
          </template>

          <template v-if="frequency === 'monthly'">
            <v-radio-group
              v-model="monthlyOption"
              color="accent"
              @update:model-value="generateRRule"
            >
              <v-radio value="specific_day">
                <template #label>
                  <span class="mr-2"> Am </span>
                  <v-number-input
                    v-model="monthlyDay"
                    density="compact"
                    variant="outlined"
                    color="accent"
                    max-width="70px"
                    hide-details
                    control-variant="hidden"
                    :min="1"
                    :max="31"
                    suffix="."
                    @update:model-value="generateRRule"
                  />
                  <span class="mx-2"> Tag, alle </span>
                  <v-number-input
                    v-model="monthlyIntervalOption1"
                    density="compact"
                    variant="outlined"
                    color="accent"
                    max-width="70px"
                    hide-details
                    control-variant="hidden"
                    :min="1"
                    :max="99"
                    @update:model-value="generateRRule"
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
                    density="compact"
                    variant="outlined"
                    hide-details
                    color="accent"
                    max-width="120px"
                    class="mr-2"
                    @update:model-value="generateRRule"
                  />
                  <v-select
                    v-model="monthlyRelativeDay"
                    :items="relativeWeekdayOptions"
                    density="compact"
                    variant="outlined"
                    hide-details
                    color="accent"
                    max-width="150px"
                    class="mr-2"
                    @update:model-value="generateRRule"
                  />
                  <span class="mr-2"> jeden </span>
                  <v-number-input
                    v-model="monthlyIntervalOption2"
                    density="compact"
                    variant="outlined"
                    color="accent"
                    max-width="70px"
                    hide-details
                    control-variant="hidden"
                    :min="1"
                    :max="99"
                    @update:model-value="generateRRule"
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
</template>

<script setup lang="ts">
import { RRule, Weekday } from "rrule";
import { onMounted, ref, watch } from "vue";

import CardForm from "@/components/common/CardForm.vue";

interface SelectOption<T> {
  value: T;
  title: string;
}

// Der externe RRule-String (v-model)
const modelValue = defineModel<string>("");

const isInternalChange = ref(false);

type FrequencyType = "daily" | "weekly" | "monthly";
const frequency = ref<FrequencyType>("weekly");
const frequencyOptions: SelectOption<FrequencyType>[] = [
  { title: "Täglich", value: "daily" },
  { title: "Wöchentlich", value: "weekly" },
  { title: "Monatlich", value: "monthly" },
];

// --- DAILY STATE ---
type DailyOption = "every" | "workdays";
const dailyOption = ref<DailyOption>("every");
const dailyInterval = ref<number>(1);

// --- WEEKLY STATE ---
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

// --- MONTHLY STATE ---
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

// Hilfs-Mapping GUI-String -> RRule Weekday Objekt
const rruleWeekdayMap: Record<DayType, Weekday> = {
  MO: RRule.MO,
  TU: RRule.TU,
  WE: RRule.WE,
  TH: RRule.TH,
  FR: RRule.FR,
  SA: RRule.SA,
  SU: RRule.SU,
};

// Hilfs-Mapping RRule-Zahl (0-6) -> GUI-String
const numToDayStr: Record<number, DayType> = {
  0: "MO",
  1: "TU",
  2: "WE",
  3: "TH",
  4: "FR",
  5: "SA",
  6: "SU",
};

/**
 * Generiert aus dem aktuellen GUI-State das RRule-Objekt und setzt das modelValue.
 */
const generateRRule = () => {
  const options: any = {};

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
      options.bymonthday = monthlyDay.value;
      options.interval = monthlyIntervalOption1.value;
    } else {
      options.interval = monthlyIntervalOption2.value;
      options.bysetpos = parseInt(monthlyRelativePosition.value);

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

  try {
    const rule = new RRule(options);
    const newRuleString = rule.toString();

    // 1. Flag setzen: "Die nächste Änderung an modelValue kommt von uns!"
    isInternalChange.value = true;

    // 2. Wert updaten (das triggert den Watcher)
    modelValue.value = newRuleString;
  } catch (e) {
    console.error("Fehler beim Generieren der RRule:", e);
  }
};

/**
 * Zerlegt einen RFC-konformen RRule-String und befüllt den GUI-State.
 */
const parseIncomingRRule = (rruleString: string) => {
  if (!rruleString) return;

  try {
    // Falls der String noch ein "RRULE:" Präfix enthält, säubern wir ihn für die Library
    const cleanString = rruleString.replace(/^RRULE:/i, "");
    const rule = RRule.fromString(cleanString);
    const orig = rule.origOptions;

    const freq = orig.freq;

    // 1. TÄGLICH / ARBEITSTAGE
    if (freq === RRule.DAILY) {
      frequency.value = "daily";
      dailyOption.value = "every";
      dailyInterval.value = orig.interval || 1;
    }

    // 2. WÖCHENTLICH (und Sonderfall Arbeitstage)
    else if (freq === RRule.WEEKLY) {
      const byweekdayArray = orig.byweekday
        ? Array.isArray(orig.byweekday)
          ? orig.byweekday
          : [orig.byweekday]
        : [];
      // Extrahiere numerische Werte (0 = MO, 6 = SO)
      const weekdayNums = byweekdayArray
        .map((d: any) => (typeof d === "number" ? d : d.weekday))
        .sort();
      const isWorkdays =
        weekdayNums.length === 5 && weekdayNums.every((v, i) => v === i); // 0,1,2,3,4 -> MO-FR

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
    }

    // 3. MONATLICH
    else if (freq === RRule.MONTHLY) {
      frequency.value = "monthly";

      // Unterscheidung spezifisch vs. relativ anhand der Attribute
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
          const weekdayNums = byweekdayArray
            .map((d: any) => (typeof d === "number" ? d : d.weekday))
            .sort();

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
          monthlyDay.value = mday;
        }
      }
    }
  } catch (e) {
    console.error("Fehler beim Parsen der RRule:", e);
  }
};

// Einmaliges Parsen beim Laden der Komponente
watch(modelValue, (newValue) => {
  // Wenn die Änderung durch generateRRule() ausgelöst wurde:
  if (!isInternalChange.value) {
    parseIncomingRRule(newValue || "");
    // Flag zurücksetzen und abbrechen – wir müssen nichts parsen!
  }
  // Wenn wir hier ankommen, kam der String von außen (Parent-Komponente)
  console.log("RRule updated");
  isInternalChange.value = false;
});

onMounted(() => {
  console.log("RRule updated", modelValue.value);

  if (modelValue.value) {
    // Flag hier nicht nötig, da wir beim initialen Parsen modelValue nicht verändern,
    // sondern nur unsere internen Refs setzen.
    parseIncomingRRule(modelValue.value);
  }
});
</script>

<style scoped></style>
