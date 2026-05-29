<template>
  <card-form
    subtitle="Serienelemente bearbeiten"
    class="h-100"
  >
    <template #text>
      <v-dialog
        v-model="isDialogOpen"
        persistent
        max-width="600px"
        @after-leave="clearAppointmentData"
      >
        <confirm-card
          title="Einzeltermin bearbeiten"
          :loading="updateAppointmentLoading"
          @cancel="reset"
        >
          <template #confirm="{ disabled }">
            <base-button
              :disabled="disabled"
              text="Speichern"
              @click="saveAppointment"
            />
          </template>
          <template #text="{ disabled }">
            <schedule-template-form
              v-if="appointmentToEdit"
              v-model="appointmentToEdit.schedule"
              :disabled="disabled"
            />
          </template>
        </confirm-card>
      </v-dialog>
      <v-list>
        <v-list-item
          v-for="appointment in modelValue"
          :key="appointment.id"
          border
          rounded
          class="mb-2 pa-2 pl-4"
        >
          <v-list-item-title>
            <v-row justify="space-between">
              <v-col align-self="center">
                <date-display
                  :display-date="appointment.schedule.occupancyStart"
                />
              </v-col>
              <v-col cols="auto">
                <base-button
                  secondary
                  variant="tonal"
                  :append-icon="mdiPencilOutline"
                  text="Bearbeiten"
                  @click="openEditDialog(appointment)"
                />
              </v-col>
            </v-row>
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </template>
  </card-form>
</template>

<script setup lang="ts">
import type {
  AppointmentMinimalResponseDTO,
  ScheduleTemplate,
} from "@/api/raumreservierung-backend";

import { mdiPencilOutline } from "@mdi/js";
import { ref, toRaw } from "vue";

import ScheduleTemplateForm from "@/components/booking/ScheduleTemplateForm.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CardForm from "@/components/common/CardForm.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";
import DateDisplay from "@/components/common/date/DateDisplay.vue";
import { useUpdateAppointment } from "@/composables/api/useAppointmentApi.ts";

const isDialogOpen = ref(false);
const appointmentToEdit = ref<AppointmentMinimalResponseDTO | undefined>(
  undefined
);

const modelValue = defineModel<AppointmentMinimalResponseDTO[]>({
  required: true,
});

defineProps<{
  schedule: ScheduleTemplate;
}>();

const {
  call: updateAppointment,
  loading: updateAppointmentLoading,
  data: updatedAppointment,
} = useUpdateAppointment();

const openEditDialog = (appointment: AppointmentMinimalResponseDTO) => {
  appointmentToEdit.value = appointment;
  isDialogOpen.value = true;
};

const saveAppointment = async () => {
  if (!appointmentToEdit.value) {
    return;
  }

  await updateAppointment({
    appointmentId: appointmentToEdit.value.id,
    appointmentRequestDTO: appointmentToEdit.value,
  });

  const indexToUpdate = modelValue.value.findIndex(
    (el) => el.id === appointmentToEdit.value?.id
  );

  if (indexToUpdate !== -1 && updatedAppointment.value) {
    const updatedList = [...modelValue.value];
    updatedList[indexToUpdate] = structuredClone(
      toRaw(updatedAppointment.value)
    );

    modelValue.value = updatedList;
  }

  reset();
};

const reset = () => {
  isDialogOpen.value = false;
};

const clearAppointmentData = () => {
  appointmentToEdit.value = undefined;
};
</script>

<style scoped></style>
