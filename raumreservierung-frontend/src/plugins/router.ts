// Composables
import type { RouteRecordRaw } from "vue-router";

import { unref } from "vue";
import { createRouter, createWebHistory } from "vue-router";

import pinia from "@/plugins/pinia";
import { useUserStore } from "@/stores/user.ts";
import { ROUTES } from "@/types/Routes.ts";
import { hasPrivileges } from "@/util/privilegeUtility.ts";
import GetStartedView from "@/views/GetStartedView.vue";
import Help from "@/views/Help.vue";
import HomeView from "@/views/HomeView.vue";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: ROUTES.HOME,
    component: HomeView,
    meta: {},
  },
  {
    path: "/help",
    name: ROUTES.HELP,
    component: Help,
    meta: {},
  },
  {
    path: "/getstarted",
    name: ROUTES.GETSTARTED,
    component: GetStartedView,
    meta: {
      requiredPrivileges: ["rooms:write"],
    },
  },
  { path: "/:catchAll(.*)*", redirect: "/" }, // CatchAll route
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return {
      top: 0,
      left: 0,
    };
  },
});

/**
 * Navigation guard checking priviledges
 */
router.beforeEach((to) => {
  const userStore = useUserStore(pinia);
  if (!to.meta.requiredPrivileges) return;

  const convertedPrivileges = Array.isArray(to.meta.requiredPrivileges)
    ? to.meta.requiredPrivileges
    : [to.meta.requiredPrivileges];

  return hasPrivileges(unref(userStore.getPrivileges()), convertedPrivileges);
});

export default router;
