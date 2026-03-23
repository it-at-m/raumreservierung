<template>
  <div>
    <base-view
      :header-text="
        t('generics.manage', {
          domain: isInternalPath
            ? t('domain.internalPerson.header', { count: 2 })
            : t('domain.externalPerson.header', { count: 2 }),
        })
      "
    >
      <template #default>
        <v-text-field
          variant="outlined"
          :label="t('common.search')"
          clearable
          @click:clear="fetchPage"
          @update:model-value="updateSearchNameAndLoadPage"
        />
        <crud-card
          ref="crudRef"
          :empty-item-template="EMPTY_ITEM_TEMPLATE"
          :domain="
            t('generics.manage', {
              domain: isInternalPath
                ? t('domain.internalPerson.header', { count: 2 })
                : t('domain.externalPerson.header', { count: 2 }),
            })
          "
          :loading="personPageLoading || deletePersonLoading"
          @update:options="updateOptionsAndLoadPage"
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
          <template #tableActions="{ openCreate }">
            <base-button
              :disabled="isInternalPath"
              @click="openCreate"
              :append-icon="mdiPlus"
              :text="t('common.add')"
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
              @update:options="updateOptionsAndLoadPage"
            >
              <template
                v-if="!isInternalPath"
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
        </crud-card>
      </template>
    </base-view>
  </div>
</template>

<script setup lang="ts">
import type {
  ExternalPersonResponseDto,
  InternalPersonResponseDto,
  InternalPersonResponseDtoTypeEnum,
} from "@/api/raumreservierung-backend";
import type { TableHeader } from "@/types/TableHeader.ts";

import { mdiPlus } from "@mdi/js";
import { useDebounceFn } from "@vueuse/core";
import { computed, ref, useTemplateRef } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute } from "vue-router";

import { Levels } from "@/api/error.ts";
import { ExternalPersonResponseDtoTypeEnum } from "@/api/raumreservierung-backend";
import BaseView from "@/components/common/BaseView.vue";
import ActionButton from "@/components/common/buttons/ActionButton.vue";
import BaseButton from "@/components/common/buttons/BaseButton.vue";
import CrudCard from "@/components/common/CrudCard.vue";
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
  searchName: string | undefined;
  itemsPerPage: number;
  sortBy: SortItem[];
}

const EMPTY_ITEM_TEMPLATE = {
  name: "",
} as ExternalPersonResponseDto | InternalPersonResponseDto;

// --- State ---
const currentPageOptions = ref<LoadEntriesOptions>({
  page: 1,
  itemsPerPage: 10,
  searchName: undefined,
  sortBy: [{ key: "name", order: "asc" }],
});

const snackbarStore = useSnackbarStore();

const crudRef = useTemplateRef("crudRef");

const isInternalPath = computed(() => route.name === ROUTES.INTERNAL_PERSON);

const personType = computed(
  (): ExternalPersonResponseDtoTypeEnum | InternalPersonResponseDtoTypeEnum =>
    isInternalPath.value ? "INTERNAL" : "EXTERNAL"
);

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

// --- Functions ---
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
  await fetchPage(); // reload current page
  if (crudRef.value) {
    crudRef.value.closeDialog();
  }
  snackbarStore.add({ message: msg, level: Levels.SUCCESS });
};

const updateOptionsAndLoadPage = async (options: LoadEntriesOptions) => {
  currentPageOptions.value = {
    ...options,
    searchName: currentPageOptions.value.searchName,
  };
  await fetchPage();
};

const debouncedFetchPage = useDebounceFn(async () => {
  await fetchPage();
}, 1000);

const updateSearchNameAndLoadPage = async (searchName: string | undefined) => {
  currentPageOptions.value = {
    ...currentPageOptions.value,
    searchName: searchName,
  };
  await debouncedFetchPage();
};

const fetchPage = async () => {
  const sort =
    currentPageOptions.value.sortBy.length > 0
      ? currentPageOptions.value.sortBy.map(
          (item) => `${item.key},${item.order}`
        )
      : [];

  await getPersonPage({
    page: currentPageOptions.value.page - 1,
    sort,
    searchName: currentPageOptions.value.searchName,
    size: currentPageOptions.value.itemsPerPage,
    personType: personType.value,
  });
};

const headers = computed<
  TableHeader<ExternalPersonResponseDto | InternalPersonResponseDto>[]
>(() => [
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

  ...(!isInternalPath.value
    ? [
        {
          title: t("domain.externalPerson.company"),
          value: "company",
          sortable: true,
        },
        {
          title: t("domain.externalPerson.streetAddress"),
          value: "streetAddress",
          sortable: true,
        },
        {
          title: t("domain.externalPerson.postalCodeCity"),
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
          title: t("domain.internalPerson.organisationUnit"),
          value: "organisationUnit",
        },
        {
          title: t("domain.internalPerson.roleFunction"),
          value: "roleFunction",
        },
      ]),
]);
</script>

<style scoped></style>
