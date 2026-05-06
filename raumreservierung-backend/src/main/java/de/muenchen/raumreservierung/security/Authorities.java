package de.muenchen.raumreservierung.security;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Each possible authority in this project is represented by a constant in this class.
 * The constants are used within the {@link org.springframework.stereotype.Controller} or
 * {@link org.springframework.stereotype.Service} classes in the method security annotations
 * (e.g. {@link PreAuthorize}).
 */
@SuppressWarnings({"PMD.DataClass", "PMD.AvoidDuplicateLiterals"})
public final class Authorities {
    public static final String EQUIPMENT_MANAGE = "hasAnyRole('raumadmin')";
    public static final String HOLIDAYS_MANAGE = "hasAnyRole('raumadmin')";
    public static final String SEATING_MANAGE = "hasAnyRole('raumadmin')";
    public static final String ROOM_MANAGE = "hasAnyRole('raumadmin')";
    public static final String USERS_MANAGE = "hasAnyRole('raumadmin')";

    private Authorities() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
