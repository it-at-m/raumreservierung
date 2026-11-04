import "vue-i18n";
import type deApp from "@/locales/de.json"; // Pfad an euer Projekt anpassen

declare module "vue-i18n" {
  // Interface-Merging is necessary for Module Augmentation
  // eslint-disable-next-line @typescript-eslint/no-empty-object-type
  export interface DefineLocaleMessage extends MessageSchema {}
  // eslint-disable-next-line @typescript-eslint/no-empty-object-type
  export interface DefineDateTimeFormat extends DateTimeSchema {}
}

type DateTimeSchema = (typeof datetimeFormats)["de"];
type MessageSchema = typeof deApp;
