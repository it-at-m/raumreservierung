package de.muenchen.raumreservierung.security;

import java.util.List;
import java.util.stream.IntStream;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

public final class Roles {
    public static final String ANWENDER = "ROLE_anwender";
    public static final String LESEBERECHTIGT = "ROLE_leseberechtigt";
    public static final String TERMIN_ORGANISATOR = "ROLE_terminorganisator";
    public static final String RAUM_BUCHUNG = "ROLE_raumbuchung";
    public static final String RAUM_ADMIN = "ROLE_raumadmin";

    /**
     * The order of roles in this list is crucial for building the hierarchy:
     * Each role implies the previous one (e.g., the role at index i implies the role at index i-1).
     * High-privilege roles must be placed at the end of the list.
     */
    private static final List<String> ALL_ROLES = List.of(
            ANWENDER, LESEBERECHTIGT, TERMIN_ORGANISATOR, RAUM_BUCHUNG, RAUM_ADMIN);

    private Roles() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static RoleHierarchy buildRoleHierarchy() {
        final RoleHierarchyImpl.Builder builder = RoleHierarchyImpl.withRolePrefix("");

        IntStream.range(1, ALL_ROLES.size())
                .forEach(i -> builder.role(ALL_ROLES.get(i))
                        .implies(ALL_ROLES.get(i - 1)));

        return builder.build();
    }
}
