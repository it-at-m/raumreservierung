<template>
  <v-btn
    :color="secondary ? '' : 'secondary'"
    :variant="secondary ? 'outlined' : 'flat'"
    :disabled="disabled"
    @click="emit('click')"
  >
    <template #default>
      <slot name="default">
        {{ text }}
      </slot>
    </template>
    <template #prepend>
      <slot name="prepend">
        <v-icon
          v-if="prependIcon"
          :icon="prependIcon"
        />
      </slot>
    </template>
    <template #append>
      <slot name="append">
        <v-icon
          v-if="appendIcon"
          :icon="appendIcon"
        />
      </slot>
    </template>
  </v-btn>
</template>

<script setup lang="ts">
import { VBtn } from "vuetify/components";

type VBtnProps = InstanceType<typeof VBtn>["$props"];
type VuetifyIcon = VBtnProps["prependIcon"];

const { secondary = false } = defineProps<{
  text?: string;
  prependIcon?: VuetifyIcon;
  appendIcon?: VuetifyIcon;

  secondary?: boolean;
  disabled?: boolean;
}>();

const emit = defineEmits<(e: "click") => void>();

defineSlots<{
  default(): void;
  append(): void;
  prepend(): void;
}>();
</script>
<style scoped></style>
