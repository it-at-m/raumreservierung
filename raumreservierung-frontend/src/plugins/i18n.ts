// RRULe uses its own translation and needs to be extracted into a computed property if changing of the language is needed!
import type { Weekday } from "rrule";
import type { Language } from "rrule/dist/esm/nlp/i18n";

import { createI18n } from "vue-i18n";
import { de as deVuetify } from "vuetify/locale";

import deApp from "@/locales/de.json";

type MessageSchema = typeof deApp;
type Locales = "de";

const messages = {
  de: {
    $vuetify: {
      ...deVuetify,
    },
    ...deApp,
  },
};

const datetimeFormats = {
  de: {
    // TT.MM.JJJJ
    short: {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
    },
    // TT.MM.JJJJ HH24:MM
    long: {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    },
    // TT.MM.JJJJ HH24:MM:SS
    timestamp: {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    },
  },
};

export default createI18n<[MessageSchema], Locales>({
  legacy: false,
  locale: "de",
  fallbackLocale: "de",
  messages,
  // @ts-expect-error false positive for type mismatch (no tsc compilation error)
  datetimeFormats,
});

const germanStrings: Record<string, string> = {
  every: "jeden",
  until: "bis",
  day: "Tag",
  days: "Tage",
  week: "Woche",
  weeks: "Wochen",
  month: "Monat",
  months: "Monate",
  year: "Jahr",
  years: "Jahre",
  on: "am",
  at: "um",
  the: "den",
  first: "ersten",
  second: "zweiten",
  third: "dritten",
  nth: "%{n}.",
  last: "letzten",
  for: "für",
  time: "Mal",
  times: "Mal",
  and: "und",
  or: "oder",
  "days, ": "Tage, ",
  "weeks, ": "Wochen, ",
  "months, ": "Monate, ",
  "years, ": "Jahre, ",
};

export const rruleGetText = (id: string | number | Weekday): string => {
  const key = String(id);
  return germanStrings[key] || key;
};

export const rruleDeLanguage: Language = {
  dayNames: [
    "Sonntag",
    "Montag",
    "Dienstag",
    "Mittwoch",
    "Donnerstag",
    "Freitag",
    "Samstag",
  ],
  monthNames: [
    "Januar",
    "Februar",
    "März",
    "April",
    "Mai",
    "Juni",
    "Juli",
    "August",
    "September",
    "Oktober",
    "November",
    "Dezember",
  ],
  tokens: {
    SKIP: /^[ \r\n\t]+|^\.$/,
    number: /^[1-9][0-9]*/,
    numberAsText: /^(eins|zwei|drei)/i,
    every: /^jeden?/i,
    "day(s)": /^Tage?/i,
    "weekday(s)": /^Wochentage?/i,
    "week(s)": /^Wochen?/i,
    "hour(s)": /^Stunden?/i,
    "minute(s)": /^Minuten?/i,
    "month(s)": /^Monate?/i,
    "year(s)": /^Jahre?/i,
    on: /^(am|im)/i,
    at: /^(um)/i,
    the: /^den/i,
    first: /^ersten/i,
    second: /^zweiten/i,
    third: /^dritten/i,
    nth: /^([1-9][0-9]*)\./i,
    last: /^letzten/i,
    for: /^für/i,
    "time(s)": /^Mal/i,
    until: /^bis/i,
    Monday: /^Mo(n(tag)?)?/i,
    Tuesday: /^Di(e(nstag)?)?/i,
    Wednesday: /^Mi(t(twoch)?)?/i,
    Thursday: /^Do(n(nerstag)?)?/i,
    Friday: /^Fr(e(itag)?)?/i,
    Saturday: /^Sa(m(stag)?)?/i,
    Sunday: /^So(n(ntag)?)?/i,
    January: /^Jan(uar)?/i,
    February: /^Feb(ruar)?/i,
    March: /^März/i,
    April: /^Apr(il)?/i,
    May: /^Mai/i,
    June: /^Jun(i)?/i,
    July: /^Jul(i)?/i,
    August: /^Aug(ust)?/i,
    September: /^Sep(tember)?/i,
    October: /^Okt(ober)?/i,
    November: /^Nov(ember)?/i,
    December: /^Dez(ember)?/i,
    comma: /^(,\s*|(und|oder)\s*)+/i,
  },
};
