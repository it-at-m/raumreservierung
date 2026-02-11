import "vue-router";

import type { Privilege } from "@/types/Privilege.ts";

export {};

declare module "vue-router" {
  interface RouteMeta {
    requiredPrivileges?: Privilege | Privilege[];
  }
}
