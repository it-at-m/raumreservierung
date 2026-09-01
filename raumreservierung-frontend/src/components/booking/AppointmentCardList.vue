<template>
  <card-form
    :subtitle="
      t('components.appointmentCardList.editSeriesElement', { count: 2 })
    "
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
          :title="t('components.appointmentCardList.editSeriesElement')"
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
      <v-infinite-scroll
        class="text-medium-emphasis"
        :items="currentAppointments"
        mode="manual"
        max-height="450px"
        @load="onLoadMore"
      >
        <template #default>
          <v-list-item
            v-for="appointment in currentAppointments"
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
        </template>
        <template #load-more="{ props }">
          <base-button
            v-if="nextAppointmentPage < totalPages"
            secondary
            :loading="appointmentsLoading"
            :text="t('components.appointmentCardList.loadMoreSeriesElements')"
            v-bind="props"
          />
        </template>
      </v-infinite-scroll>
    </template>
  </card-form>
</template>

<script setup lang="ts">
import type {
  AppointmentDetailsResponseDTO,
  ScheduleTemplate,
} from "@/api/raumreservierung-backend";
import type { InfiniteScrollLoad } from "@/types/InfiniteScroll.ts";

import { mdiPencilOutline } from "@mdi/js";
import { ref, toRaw, watch } from "vue";
import { useI18n } from "vue-i18n";

import ScheduleTemplateForm from "@/components/booking/ScheduleTemplateForm.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CardForm from "@/components/common/CardForm.vue";
import ConfirmCard from "@/components/common/ConfirmCard.vue";
import DateDisplay from "@/components/common/date/DateDisplay.vue";
import {
  useGetAppointments,
  useUpdateAppointment,
} from "@/composables/api/useAppointmentApi.ts";

const { t } = useI18n();

const { bookingId } = defineProps<{
  bookingId?: string;
  schedule: ScheduleTemplate;
}>();

const currentAppointments = ref<AppointmentDetailsResponseDTO[]>([]);

// Pagination of appointments
const nextAppointmentPage = ref(0);
const totalPages = ref(1);

const {
  call: getAppointmentPage,
  data: appointmentPage,
  loading: appointmentsLoading,
  error: appointmentsError,
} = useGetAppointments();

const fetchPage = async () => {
  if (!bookingId) {
    return;
  }

  await getAppointmentPage({
    page: nextAppointmentPage.value,
    startDate: new Date(),
    bookingId: bookingId,
    endDate: new Date(new Date().setFullYear(new Date().getFullYear() + 5)),
    size: 5,
    sort: ["schedule.occupancyStart,asc"],
  });
};

watch(
  () => bookingId,
  async (newBookingId) => {
    if (!newBookingId) {
      currentAppointments.value = [];
      nextAppointmentPage.value = 0;
      return;
    }

    nextAppointmentPage.value = 0;
    await fetchPage();

    if (appointmentPage.value?.content && !appointmentsError.value) {
      currentAppointments.value = structuredClone(
        toRaw(appointmentPage.value.content)
      );
      totalPages.value = appointmentPage.value.page?.totalPages || 1;
      nextAppointmentPage.value++;
    }
  },
  {
    immediate: true,
  }
);

const onLoadMore = async (event: InfiniteScrollLoad) => {
  const { done } = event;

  if (!bookingId || nextAppointmentPage.value >= totalPages.value) {
    done("empty");
    return;
  }

  await fetchPage();

  if (appointmentPage.value?.content && !appointmentsError.value) {
    const newItems = structuredClone(toRaw(appointmentPage.value.content));
    currentAppointments.value = [...currentAppointments.value, ...newItems];
    nextAppointmentPage.value++;
    done("ok");
  } else {
    done("error");
  }
};

// Appointment editing
const isDialogOpen = ref(false);
const appointmentToEdit = ref<AppointmentDetailsResponseDTO | undefined>(
  undefined
);

const {
  call: updateAppointment,
  loading: updateAppointmentLoading,
  data: updatedAppointment,
} = useUpdateAppointment();

const openEditDialog = (appointment: AppointmentDetailsResponseDTO) => {
  appointmentToEdit.value = appointment;
  isDialogOpen.value = true;
};

const saveAppointment = async () => {
  if (!appointmentToEdit.value) return;

  await updateAppointment({
    appointmentId: appointmentToEdit.value.id,
    appointmentRequestDTO: appointmentToEdit.value,
  });

  const updatedSchedule = updatedAppointment.value?.schedule;

  if (updatedSchedule) {
    currentAppointments.value = currentAppointments.value.map((item) => {
      if (item.id === appointmentToEdit.value?.id) {
        return {
          ...item,
          schedule: structuredClone(toRaw(updatedSchedule)),
        };
      }
      return item;
    });
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
