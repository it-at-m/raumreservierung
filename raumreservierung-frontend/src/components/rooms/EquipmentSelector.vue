<template>
  <card-form
    :subtitle="t('domain.equipment.header')"
    :loading="allEquipmentLoading || loading"
  >
    <template #text>
      <v-row density="compact">
        <template v-if="!(allEquipmentLoading || loading)">
          <v-col
            v-for="equip in computedEquipment"
            :key="equip.id"
            cols="12"
            md="6"
            lg="4"
          >
            <v-checkbox
              v-model="modelValue"
              :value="equip.id"
              class="w-100 py-0"
              color="accent"
              density="compact"
              :disabled="loading"
              hide-details
            >
              <template #label>
                <div
                  v-tooltip:end="equip.description"
                  :class="{ 'text-grey': !equip.isActive }"
                >
                  {{ equip.name }}
                </div>
              </template>
            </v-checkbox>
          </v-col>
        </template>
      </v-row>
    </template>
    <template
      v-if="!disableAddition"
      #actions
    >
      <div class="w-100 d-flex justify-end">
        <v-dialog
          max-width="900"
          persistent
        >
          <template #activator="{ props: activatorProps }">
            <base-button
              v-bind="activatorProps"
              class="mb-2 mr-2"
              secondary
              :prepend-icon="mdiPlus"
              :text="
                t('generics.add', { domain: t('domain.equipment.header') })
              "
            />
          </template>
          <template #default="{ isActive }">
            <room-resource-management-card
              :item-list="allEquipment"
              :loading="
                allEquipmentLoading || createEquipmentLoading || loading
              "
              :domain="t('domain.equipment.header')"
              @update-items="getAllEquipment"
              @close="isActive.value = false"
              @create="handleCreate"
            />
          </template>
        </v-dialog>
      </div>
    </template>
  </card-form>
</template>

<script setup lang="ts">
import type { EquipmentResponseDto } from "@/api/raumreservierung-backend";

import { mdiPlus } from "@mdi/js";
import { computed, onMounted } from "vue";
import { useI18n } from "vue-i18n";

import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CardForm from "@/components/common/CardForm.vue";
import RoomResourceManagementCard from "@/components/rooms/RoomResourceManagementCard.vue";
import {
  useCreateEquipment,
  useGetAllEquipments,
} from "@/composables/api/useEquipmentApi.ts";

const { t } = useI18n();

const modelValue = defineModel<string[]>();

const {
  filterIds = [],
  disableAddition = false,
  loading = false,
} = defineProps<{
  filterIds?: string[];
  disableAddition?: boolean;
  loading?: boolean;
}>();

const {
  call: getAllEquipment,
  data: allEquipment,
  loading: allEquipmentLoading,
} = useGetAllEquipments();

const computedEquipment = computed<EquipmentResponseDto[]>(() =>
  filterIds.length === 0
    ? allEquipment.value
    : allEquipment.value.filter(
        (equip) => equip.id && filterIds.includes(equip.id)
      )
);

const { call: createEquipment, loading: createEquipmentLoading } =
  useCreateEquipment();

onMounted(() => getAllEquipment());

const handleCreate = async (newItemName: string) => {
  await createEquipment({
    equipmentRequestDto: {
      name: newItemName,
      description: "",
      isActive: true,
    },
  });

  await getAllEquipment();
};
</script>

<style scoped></style>
