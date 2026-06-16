<template>
  <span>
    {{ t("components.dateDisplay", { day, date }) }}
  </span>
</template>

<script setup lang="ts">
import { useDateFormat } from "@vueuse/core";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const { displayDate } = defineProps<{
  displayDate: Date;
}>();

const day = useDateFormat(displayDate, "dddd");

const date = useDateFormat(displayDate, "DD. MMM YYYY");
</script>

<style scoped></style>
