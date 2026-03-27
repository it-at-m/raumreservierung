export type Action = "read" | "write" | "manage" | "self";

interface ActionMap {
  rooms: Extract<Action, "read" | "write">;
  equipment: Extract<Action, "write">;
  seating: Extract<Action, "write">;
  days: Extract<Action, "write">;
  users: Extract<Action, "manage">;
  bookings: Extract<Action, "read" | "write" | "manage" | "self">;
  calendar: Extract<Action, "read" | "write">;
}

export const PRIVILEGE_DIVIDER = ":" as const;

export type Privilege = {
  [R in keyof ActionMap]: `${R}${typeof PRIVILEGE_DIVIDER}${ActionMap[R]}`;
}[keyof ActionMap];

export type PrivilegeKey = Privilege extends `${infer K}:${string}` ? K : never;

export type PrivilegeAction = Privilege extends `${string}:${infer A}`
  ? A
  : never;
