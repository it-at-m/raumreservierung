export type Role =
  | "raumadmin"
  | "raumbuchung"
  | "terminorganisator"
  | "leseberechtigt"
  | "anwender";

export const ROLE_WEIGHTS: Record<Role, number> = {
  raumadmin: 5,
  raumbuchung: 4,
  terminorganisator: 3,
  leseberechtigt: 2,
  anwender: 1,
};

export function isRole(role: string): role is Role {
  return role in ROLE_WEIGHTS;
}
