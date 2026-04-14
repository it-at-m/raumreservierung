<template>
  <v-btn
    variant="text"
    :disabled="disabled"
    :icon="computedIcon"
    @click.stop="emit('click')"
  />
</template>

<script setup lang="ts">
import { mdiEyeOutline, mdiPencil, mdiTrashCanOutline } from "@mdi/js";
import { computed } from "vue";

type Type = "edit" | "delete" | "readOnly";

const { type, disabled = false } = defineProps<{
  disabled?: boolean;
  type?: Type;
}>();

const computedIcon = computed(() => {
  switch (type) {
    case "delete":
      return mdiTrashCanOutline;
    case "readOnly":
      return mdiEyeOutline;
    default:
      return mdiPencil;
  }
});

const emit = defineEmits<(e: "click") => void>();
</script>

<style scoped></style>
