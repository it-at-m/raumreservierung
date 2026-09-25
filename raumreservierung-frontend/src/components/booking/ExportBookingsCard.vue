<template>
  <confirm-card
    title="Welches Jahr an Buchungen wollen Sie exportieren?"
    @confirm="emit('close')"
    @cancel="emit('close')"
  >
    <template #text>
      <year-slider
        v-model="selectedYear"
        class="rounded border-md"
        :start-year="currentYear - PREVIOUS_YEARS"
        :end-year="currentYear"
      />
    </template>
    <template #confirm="{ props }">
      <base-button
        :append-icon="mdiExport"
        text="Exportieren"
        @click="
          redirectToDownload();
          props.onClick();
        "
      />
    </template>
  </confirm-card>
</template>

<script setup lang="ts">
import { mdiExport } from "@mdi/js";
import { computed, ref } from "vue";
import { useRouter } from "vue-router";

import { ExportControllerApi } from "@/api/raumreservierung-backend";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";
import YearSlider from "@/components/common/YearSlider.vue";
import { BASE_PATH } from "@/constants.ts";
import { ApiFactory } from "@/util/apiFactory.ts";

const PREVIOUS_YEARS = 5;
const router = useRouter();

const currentYear = computed(() => new Date().getFullYear());
const selectedYear = ref(new Date().getFullYear());

const emit = defineEmits<{
  close: [];
}>();

const redirectToDownload = async () => {
  const exportApi = ApiFactory.getInstance(ExportControllerApi);

  const requestOpts = await exportApi.exportBookingsCsvRequestOpts({
    year: selectedYear.value,
  });

  const routeData = router.resolve({
    path: `${BASE_PATH}${requestOpts.path}`,
    query: { year: selectedYear.value },
  });

  window.open(routeData.href, "_blank");
};
</script>

<style scoped></style>
