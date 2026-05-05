<template>
  <card-form :subtitle="t('domain.equipment.header')">
    <template #text>
      <v-row
        density="compact"
        dense
      >
        <template v-if="allEquipmentLoading">
          <v-col
            cols="12"
            md="6"
            lg="4"
            v-for="el in 5"
            :key="el"
          >
            <v-skeleton-loader type="text" />
          </v-col>
        </template>
        <template v-else>
          <v-col
            cols="12"
            md="6"
            lg="4"
            v-for="equip in allEquipment"
            :key="equip.id"
          >
            <v-checkbox
              class="w-100 py-0"
              color="accent"
              density="compact"
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
    <template #actions>
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
              :loading="allEquipmentLoading || createEquipmentLoading"
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
import { mdiPlus } from "@mdi/js";
import { onMounted } from "vue";
import { useI18n } from "vue-i18n";

import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CardForm from "@/components/common/CardForm.vue";
import RoomResourceManagementCard from "@/components/rooms/RoomResourceManagementCard.vue";
import {
  useCreateEquipment,
  useGetAllEquipments,
} from "@/composables/api/useEquipmentApi.ts";

const { t } = useI18n();

const {
  call: getAllEquipment,
  data: allEquipment,
  loading: allEquipmentLoading,
} = useGetAllEquipments();

const { call: createEquipment, loading: createEquipmentLoading } =
  useCreateEquipment();

onMounted(() => getAllEquipment());

const handleCreate = async (newItemName: string) => {
  await createEquipment({
    equipmentRequestDto: {
      name: newItemName,
      isActive: true,
    },
  });

  await getAllEquipment();
};
</script>

<style scoped></style>
