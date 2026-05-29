<template>
  <v-btn
    variant="text"
    :disabled="disabled"
    :icon="icon ?? computedIcon"
    @click.stop="emit('click')"
  />
</template>

<script setup lang="ts">
import { mdiPencil, mdiPlus, mdiTrashCanOutline } from "@mdi/js";
import { computed } from "vue";

type Type = "edit" | "delete" | "add";

const { type, disabled = false } = defineProps<{
  disabled?: boolean;
  icon?: string;
  type?: Type;
}>();

const computedIcon = computed(() => {
  switch (type) {
    case "add":
      return mdiPlus;
    case "delete":
      return mdiTrashCanOutline;
    default:
      return mdiPencil;
  }
});

const emit = defineEmits<(e: "click") => void>();
</script>

<style scoped></style>
