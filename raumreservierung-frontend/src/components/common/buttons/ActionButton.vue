<template>
  <v-btn
    variant="text"
    :disabled="disabled"
    :icon="computedIcon"
    @click="emit('click')"
  />
</template>

<script setup lang="ts">
import { mdiPencil, mdiTrashCanOutline } from "@mdi/js";
import { computed } from "vue";

type Type = "edit" | "delete";

const { type, disabled = false } = defineProps<{
  disabled?: boolean;
  type?: Type;
}>();

const computedIcon = computed(() => {
  switch (type) {
    case "delete":
      return mdiTrashCanOutline;
    default:
      return mdiPencil;
  }
});

const emit = defineEmits<(e: "click") => void>();
</script>

<style scoped></style>
