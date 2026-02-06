<template>
  <v-app>
    <the-snackbar />
    <v-app-bar color="primary">
      <v-row align="center">
        <v-col
          cols="3"
          class="d-flex align-center justify-start"
        >
          <v-app-bar-nav-icon @click.stop="toggleDrawer()" />
          <router-link to="/">
            <v-toolbar-title class="font-weight-bold">
              <span class="text-white">{{ t("app.name.part1") }}</span>
              <span class="text-secondary">{{ t("app.name.part2") }}</span>
              <span class="text-white">{{ t("app.name.part3") }}</span>
            </v-toolbar-title>
          </router-link>
        </v-col>
        <v-col
          cols="6"
          class="d-flex align-center justify-center"
        >
          <v-text-field
            id="searchField"
            v-model="query"
            flat
            variant="solo-inverted"
            hide-details
            :label="t('app.search')"
            clearable
            :prepend-inner-icon="mdiMagnify"
            theme="dark"
            @keyup.enter="search"
          />
        </v-col>
        <v-col
          cols="3"
          class="d-flex align-center justify-end"
        >
          <app-switcher
            v-if="appswitcherBaseUrl"
            :base-url="appswitcherBaseUrl"
            :tags="['global']"
            :icon="mdiApps"
          />
          <v-btn
            variant="text"
            icon
          >
            <ad2-image-avatar
              v-if="userStore.getUser !== null"
              :username="userStore.getUser.username"
            />
          </v-btn>
        </v-col>
      </v-row>
    </v-app-bar>
    <v-navigation-drawer v-model="drawer">
      <v-list>
        <v-list-item :to="{ name: ROUTES_GETSTARTED }">
          <v-list-item-title>
            {{ t("views.getStarted.navText") }}
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-main>
      <v-container fluid>
        <router-view v-slot="{ Component }">
          <v-fade-transition mode="out-in">
            <component :is="Component" />
          </v-fade-transition>
        </router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { mdiApps } from "@mdi/js";
import { AppSwitcher } from "@muenchen/appswitcher-vue";
import { useMediaQuery, useToggle } from "@vueuse/core";
import { onMounted } from "vue";
import { useI18n } from "vue-i18n";

import { Levels } from "@/api/error.ts";
import AvatarCard from "@/components/common/AvatarCard.vue";
import NavigationDrawerItems from "@/components/common/NavigationDrawerItems.vue";
import TheSnackbarQueue from "@/components/TheSnackbarQueue.vue";
import { useUserInfo } from "@/composables/api/useUserApi.ts";
import { APPSWITCHER_URL } from "@/constants";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { useUserStore } from "@/stores/user";
import { UserLocalDevelopment } from "@/types/User";

const { t } = useI18n();

const query = ref<string>("");
const appswitcherBaseUrl = APPSWITCHER_URL;

const snackbarStore = useSnackbarStore();
const userStore = useUserStore();
const [drawer, toggleDrawer] = useToggle(true);
const isSmallScreen = useMediaQuery("(max-width: 1280px)");

onMounted(() => {
  loadUser();
});

const {
  call: userInfoCall,
  data: userInfoData,
  error: userInfoError,
} = useUserInfo();

/**
 * Loads UserInfo from the backend and sets it in the store.
 */
async function loadUser() {
  // userinfo call
  await userInfoCall();
  if (userInfoError.value) {
    if (import.meta.env.DEV) {
      userStore.setUser(UserLocalDevelopment());
      snackbarStore.add({
        level: Levels.INFO,
        message: "Local Development User is used.",
      });
    } else {
      snackbarStore.add({
        level: Levels.WARNING,
        message: "Nutzer konnte nicht geladen werden.",
      });
    }
    return;
  }
  // save into store
  userStore.setUser(JSON.parse(JSON.stringify(userInfoData.value)));
}
</script>
