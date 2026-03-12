<template>
  <server-table-crud-view
    ref="crudRef"
    :empty-item-template="EMPTY_ITEM_TEMPLATE"
    :domain="personType"
    :headers="headers"
    :loading="personPageLoading || deletePersonLoading"
    :items="personPageData?.content || []"
    :items-length="personPageData?.page?.totalElements || 0"
    @update:options="updatedOptions"
    @create="handleCreate"
    @update="handleUpdate"
    @delete="handleDelete"
  >
    <template #form="{ item, updateItem, updateValidity }">
      <external-person-form
        :model-value="item"
        @update:model-value="updateItem"
        @is-valid="updateValidity"
        :disabled="updatePersonLoading || createPersonLoading"
      />
    </template>
    <template #table="{ openEdit, openDelete }">
      <v-data-table-server
        must-sort
        :sortBy="currentPageOptions.sortBy"
        :items-length="personPageData?.page?.totalElements || 0"
        :items="personPageData?.content || []"
        :headers="headers"
        :loading="personPageLoading"
        :disable-sort="personPageLoading"
        @update:options="updatedOptions"
      >
        <template
          v-if="personType === 'EXTERNAL'"
          v-slot:[`item.actions`]="{ item }"
        >
          <slot name="item.actions">
            <v-row align-content="center">
              <v-col
                class="pa-0"
                cols="12"
                sm="6"
              >
                <action-button
                  type="edit"
                  class="mr-1"
                  @click="openEdit(item)"
                />
              </v-col>
              <v-col
                class="pa-0"
                cols="12"
                sm="6"
              >
                <action-button
                  type="delete"
                  @click="openDelete(item)"
                />
              </v-col>
            </v-row>
          </slot>
        </template>
      </v-data-table-server>
    </template>
  </server-table-crud-view>
</template>

<script setup lang="ts">
import type {
  ExternalPersonResponseDto,
  InternalPersonResponseDto,
  InternalPersonResponseDtoTypeEnum,
} from "@/api/raumreservierung-backend";
import type { TableHeader } from "@/components/common/CardTable.vue";

import { computed, ref, useTemplateRef, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute } from "vue-router";

import { Levels } from "@/api/error.ts";
import { ExternalPersonResponseDtoTypeEnum } from "@/api/raumreservierung-backend";
import ActionButton from "@/components/common/buttons/ActionButton.vue";
import ServerTableCrudView from "@/components/common/ServerTableCrudView.vue";
import ExternalPersonForm from "@/components/ExternalPersonForm.vue";
import {
  useCreatePerson,
  useDeletePerson,
  useGetPersonPage,
  useUpdatePerson,
} from "@/composables/api/usePersonApi.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { ROUTES } from "@/types/Routes.ts";

const { t } = useI18n();

const route = useRoute();

interface SortItem {
  key: string;
  order: "asc" | "desc";
}

interface LoadEntriesOptions {
  page: number;
  itemsPerPage: number;
  sortBy: SortItem[];
}

const EMPTY_ITEM_TEMPLATE = {
  name: "",
} as ExternalPersonResponseDto | InternalPersonResponseDto;

const currentPageOptions = ref<LoadEntriesOptions>({
  page: 1,
  itemsPerPage: 10,
  sortBy: [{ key: "name", order: "asc" }],
});

const personType = computed(
  (): ExternalPersonResponseDtoTypeEnum | InternalPersonResponseDtoTypeEnum =>
    route.name === ROUTES.INTERNAL_PERSON ? "INTERNAL" : "EXTERNAL"
);

const snackbarStore = useSnackbarStore();

const crudRef = useTemplateRef("crudRef");

const {
  call: getPersonPage,
  data: personPageData,
  loading: personPageLoading,
} = useGetPersonPage();

const {
  call: createPerson,
  loading: createPersonLoading,
  error: createPersonError,
} = useCreatePerson();

const {
  call: updatePerson,
  loading: updatePersonLoading,
  error: updatePersonError,
} = useUpdatePerson();

const {
  call: deletePerson,
  loading: deletePersonLoading,
  error: deletePersonError,
} = useDeletePerson();

watch(
  () => personPageData.value?.content,
  () => {
    console.log(personPageData.value?.content);
  }
);

const handleCreate = async (
  newPerson: ExternalPersonResponseDto | InternalPersonResponseDto
) => {
  await createPerson({
    updatePersonRequest: { ...newPerson, type: personType.value },
  });
  if (!createPersonError.value) {
    await onSuccess(
      t("generics.created", { domain: t("domain.equipment.header") })
    );
  }
};

const handleUpdate = async (
  updatedPerson: ExternalPersonResponseDto | InternalPersonResponseDto
) => {
  if (updatedPerson.id) {
    await updatePerson({
      updatePersonRequest: { ...updatedPerson, type: personType.value },
      personId: updatedPerson.id,
    });
    if (!updatePersonError.value) {
      await onSuccess(
        t("generics.updated", { domain: t("domain.equipment.header") })
      );
    }
  }
};

const handleDelete = async (id: string) => {
  await deletePerson({ personId: id });
  if (!deletePersonError.value) {
    await onSuccess(
      t("generics.deleted", { domain: t("domain.equipment.header") })
    );
  }
};

const onSuccess = async (msg: string) => {
  await updatedOptions(currentPageOptions.value); // reload current page
  if (crudRef.value) {
    crudRef.value.closeDialog();
  }
  snackbarStore.add({ message: msg, level: Levels.SUCCESS });
};

const updatedOptions = async (
  options: LoadEntriesOptions,
  searchName: string | undefined
) => {
  currentPageOptions.value = options;

  const sort =
    options.sortBy.length > 0
      ? options.sortBy.map((item) => `${item.key},${item.order}`)
      : [];

  await getPersonPage({
    page: options.page - 1,
    sort,
    searchName,
    size: options.itemsPerPage,
    personType: personType.value,
  });
};

const headers: TableHeader<
  ExternalPersonResponseDto | InternalPersonResponseDto
> = computed(() => [
  {
    title: "Name",
    value: "name",
    sortable: true,
  },
  {
    title: "E-Mail",
    value: "email",
    sortable: true,
  },
  {
    title: "Telefonnummer",
    value: "telefonNumber",
    sortable: true,
  },

  ...(personType.value === "EXTERNAL"
    ? [
        {
          title: "Firma",
          value: "company",
          sortable: true,
        },
        {
          title: "Straße",
          value: "streetAddress",
          sortable: true,
        },
        {
          title: "PLZ",
          value: "postalCodeCity",
          sortable: true,
        },
        {
          title: t("common.action", { count: 2 }),
          value: "actions",
        },
      ]
    : [
        {
          title: "Organisationseinheit",
          value: "organisationUnit",
        },
        {
          title: "Funktion",
          value: "roleFunction",
        },
      ]),
]);
</script>

<style scoped></style>
