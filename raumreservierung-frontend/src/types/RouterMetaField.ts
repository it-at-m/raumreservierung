import "vue-router";

export type Action = "read" | "write" | "manage" | "self:read";

interface ActionMap {
  rooms: Extract<Action, "read" | "write">;
  equipment: Extract<Action, "write">;
  seating: Extract<Action, "write">;
  days: Extract<Action, "write">;
  users: Extract<Action, "write">;
  bookings: Extract<Action, "read" | "write" | "manage" | "self:read">;
  calendar: Extract<Action, "read">;
}

export type RequiredPrivileges = {
  [R in keyof ActionMap]: `${R}:${ActionMap[R]}`;
}[keyof ActionMap];

export {};

declare module "vue-router" {
  interface RouteMeta {
    requiredPrivileges?: RequiredPrivileges | RequiredPrivileges[];
  }
}
