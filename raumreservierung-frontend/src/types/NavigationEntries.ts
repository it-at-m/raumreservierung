import type { Privilege } from "@/types/Privilege.ts";
import type { RouteLocationRaw } from "vue-router";

type NavKind = "item" | "group";

interface NavBase {
  textKey: string;
  icon?: string;
  requiredPrivilege?: Privilege;
  kind: NavKind;
}

export interface NavItem extends NavBase {
  kind: "item";
  to: RouteLocationRaw;
}

export interface NavGroup extends NavBase {
  kind: "group";
  children: readonly NavItem[];
}

export type NavEntry = NavItem | NavGroup;
