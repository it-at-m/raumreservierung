import "vuetify/styles";

import type { VueI18nAdapterParams } from "vuetify/locale/adapters/vue-i18n";

import { useI18n } from "vue-i18n";
import { createVuetify } from "vuetify";
import { aliases, mdi } from "vuetify/iconsets/mdi-svg";
import { VDateInput } from "vuetify/labs/VDateInput";
import { createVueI18nAdapter } from "vuetify/locale/adapters/vue-i18n";

import i18n from "@/plugins/i18n";

export default createVuetify({
  defaults: {
    VTextField: {
      variant: "outlined",
    },
  },
  components: {
    VDateInput,
  },
  icons: {
    defaultSet: "mdi",
    aliases,
    sets: {
      mdi,
    },
  },
  theme: {
    variations: {
      colors: ["primary", "secondary", "accent", "tertiary"],
      lighten: 3,
      darken: 3,
    },
    themes: {
      light: {
        colors: {
          primary: "#333333",
          secondary: "#FFCC00",
          tertiary: "#1A3DA5",
          accent: "#7BA4D9",
          // snackbar colors
          success: "#4caf50",
          error: "#ef5350",
          info: "#03a9f4",
          warning: "#ff9800",
          // Status colors
          statusNew: "#909090",
          statusRequested: "#668bdf",
          statusApproved: "#809C00",
          statusCoordination: "#af64d4",
          statusCanceled: "#D54234",
        },
      },
      dark: {
        colors: {
          // Status colors
          statusNew: "#909090",
          statusRequested: "#668bdf",
          statusApproved: "#809C00",
          statusCoordination: "#af64d4",
          statusCanceled: "#D54234",
        },
      },
    },
  },
  locale: {
    adapter: createVueI18nAdapter({ i18n, useI18n } as VueI18nAdapterParams),
  },
});
