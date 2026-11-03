// Recreated type of vuetify calendar as it is only exported internally and cannot be imported therefore
export interface CalendarTimestamp {
  year: number;
  month: number;
  day: number;
  hour: number;
  minute: number;
  category?:
    | string
    | {
        categoryName?: string;
        name?: string;
      };
}
