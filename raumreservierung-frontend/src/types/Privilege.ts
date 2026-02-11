export type Action = "read" | "write" | "manage" | "self";

interface ActionMap {
  rooms: Extract<Action, "read" | "write">;
  equipment: Extract<Action, "write">;
  seating: Extract<Action, "write">;
  days: Extract<Action, "write">;
  users: Extract<Action, "write">;
  bookings: Extract<Action, "read" | "write" | "manage" | "self">;
  calendar: Extract<Action, "read">;
}

export type Privilege = {
  [R in keyof ActionMap]: `${R}:${ActionMap[R]}`;
}[keyof ActionMap];
