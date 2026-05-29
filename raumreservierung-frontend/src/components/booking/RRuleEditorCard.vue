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
          <!-- DAILY -->
          <template v-if="frequency === 'daily'">
            <v-radio-group
              v-model="dailyOption"
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
                    max-width="50px"
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

          <!-- WEEKLY -->
          <template v-if="frequency === 'weekly'">
            <div class="d-flex align-center">
              <span class="mr-2"> Jeden / Alle </span>
              <v-number-input
                v-model="weeklyInterval"
                density="compact"
                variant="outlined"
                color="accent"
                max-width="50px"
                hide-details
                control-variant="hidden"
                :min="1"
                :max="99"
              />
              <span class="ml-2">Woche(n)</span>
            </div>

            <v-row gap="0">
              <v-col
                v-for="weekday in weekdays"
                :key="weekday.value"
                cols="3"
              >
                <v-checkbox
                  v-model="weeklyDays"
                  :value="weekday.value"
                  density="compact"
                  :label="weekday.title"
                  hide-details
                  color="accent"
                />
              </v-col>
            </v-row>
          </template>

          <!-- MONTLY -->
          <template v-if="frequency === 'monthly'">
            <v-radio-group
              v-model="monthlyOption"
              color="accent"
            >
              <v-radio value="specific_day">
                <template #label>
                  <span class="mr-2"> Am </span>
                  <v-number-input
                    v-model="dailyInterval"
                    density="compact"
                    variant="outlined"
                    color="accent"
                    max-width="50px"
                    hide-details
                    control-variant="hidden"
                    :min="1"
                    suffix="."
                    :max="99"
                  />
                  <span class="ml-2"> Tag jedes Monats</span>
                </template>
              </v-radio>
              <v-radio value="relative_day">
                <template #label>
                  <span class="mr-2"> Am </span>
                  <v-number-input
                    v-model="dailyInterval"
                    density="compact"
                    variant="outlined"
                    color="accent"
                    max-width="50px"
                    hide-details
                    control-variant="hidden"
                    :min="1"
                    :max="99"
                  />
                  <span class="mx-2">jeden / alle </span>
                  <v-number-input
                    v-model="dailyInterval"
                    density="compact"
                    variant="outlined"
                    color="accent"
                    max-width="50px"
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
</template>

<script setup lang="ts">
import { ref } from "vue";

import CardForm from "@/components/common/CardForm.vue";

interface SelectOption<T> {
  value: T;
  title: string;
}

const modelValue = defineModel<string>("");

type FrequencyType = "daily" | "weekly" | "monthly";
const frequency = ref<FrequencyType>("weekly"); // TODO Typeable? any type of the array below?
const frequencyOptions: SelectOption<FrequencyType>[] = [
  { title: "Täglich", value: "daily" },
  { title: "Wöchentlich", value: "weekly" },
  { title: "Monatlich", value: "monthly" },
];

// DAILY STATE
type DailyOption = "every" | "workdays";
const dailyOption = ref<DailyOption>("every"); // 'every' oder 'workdays'
const dailyInterval = ref<number>(1);

// WEEKLY STATE
type DayType = "MO" | "TU" | "WE" | "TH" | "FR" | "SA" | "SU";
const weeklyInterval = ref(1);
const weeklyDays = ref<DayType[]>(["MO"]); // Array für Multi-Select Checkboxes
const weekdays: SelectOption<DayType>[] = [
  { title: "Montag", value: "MO" },
  { title: "Dienstag", value: "TU" },
  { title: "Mittwoch", value: "WE" },
  { title: "Donnerstag", value: "TH" },
  { title: "Freitag", value: "FR" },
  { title: "Samstag", value: "SA" },
  { title: "Sonntag", value: "SU" },
];

// --- Monatlich State ---
type MonthlyOption = "specific_day" | "relative_day";
const monthlyOption = ref<MonthlyOption>("specific_day"); // 'specific_day' oder 'relative_day'
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
  ...weekdays, // Montag - Sonntag importieren
];
</script>

<style scoped>
/* Hilfsklasse für saubere Abstände bei Umbrüchen (falls Flexbox wrap greift) */
.gap-2 {
  gap: 0.5rem;
}
</style>
