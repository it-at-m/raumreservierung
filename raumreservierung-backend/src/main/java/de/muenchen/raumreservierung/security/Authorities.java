package de.muenchen.raumreservierung.security;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Each possible authority in this project is represented by a constant in this class.
 * The constants are used within the {@link org.springframework.stereotype.Controller} or
 * {@link org.springframework.stereotype.Service} classes in the method security annotations
 * (e.g. {@link PreAuthorize}).
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.DataClass" })
public final class Authorities {
    //concatenation with string format
    public static final String EQUIPMENT_MANAGE = "hasRole('" + Roles.RAUM_ADMIN + "')";
    public static final String HOLIDAYS_MANAGE = "hasRole('" + Roles.RAUM_ADMIN + "')";
    public static final String SEATING_MANAGE = "hasRole('" + Roles.RAUM_ADMIN + "')";
    public static final String ROOM_MANAGE = "hasRole('" + Roles.RAUM_ADMIN + "')";
    public static final String USERS_MANAGE = "hasRole('" + Roles.RAUM_ADMIN + "')";
    public static final String BOOKING_WRITE = "hasRole('" + Roles.TERMIN_ORGANISATOR + "')";
    public static final String BOOKING_READ = "hasRole('" + Roles.LESEBERECHTIGT + "')";
    public static final String BOOKING_SELF = "hasRole('" + Roles.ANWENDER + "')";
    public static final String APPOINTMENT_WRITE = "hasRole('" + Roles.ANWENDER + "')";

    private Authorities() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
