import type { Role } from "@/types/Role.ts";

class User {
  sub = "";
  name = "";

  // LHM
  displayName = "";
  surname = "";
  telephoneNumber = "";
  email = "";
  username = "";
  givenname = "";
  department = "";
  lhmObjectID = "";
  // LHM_Extended
  preferred_username = "";
  memberof: string[] = [];
  user_roles: Role | undefined;
  authorities: string[] = [];
  resource_access?: Record<string, { roles?: string[] }>;
}

export function UserLocalDevelopment(): User {
  const u = new User();
  u.username = "Local Development User";
  u.displayName = "Local Development User";
  u.authorities = [
    // todo add authorities
  ];
  u.user_roles = "raumadmin";
  return u;
}

export default User;
