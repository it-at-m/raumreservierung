import { type VueQueryPluginOptions } from "@tanstack/vue-query";

export const vueQueryOptions: VueQueryPluginOptions = {
  queryClientConfig: {
    defaultOptions: {
      queries: {
        staleTime: 1000 * 60 * 5,
        refetchOnWindowFocus: false,
        retry: false,
        refetchOnReconnect: false,
      },
    },
  },
};
