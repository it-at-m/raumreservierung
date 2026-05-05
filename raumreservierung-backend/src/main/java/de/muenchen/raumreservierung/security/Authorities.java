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
    public static final String ANWENDER = "ROLE_anwender";
    public static final String LESEBERECHTIGT = "ROLE_leseberechtigt";
    public static final String TERMIN_ORGANISATOR = "ROLE_terminorganisator";
    public static final String RAUM_BUCHUNG = "ROLE_raumbuchung";
    public static final String RAUM_ADMIN = "ROLE_raumadmin";
    public static final String EQUIPMENT_MANAGE = "hasRole('" + RAUM_ADMIN + "')";
    public static final String HOLIDAYS_MANAGE = "hasRole('" + RAUM_ADMIN + "')";
    public static final String SEATING_MANAGE = "hasRole('" + RAUM_ADMIN + "')";
    public static final String ROOM_MANAGE = "hasRole('" + RAUM_ADMIN + "')";
    public static final String USERS_MANAGE = "hasRole('" + RAUM_ADMIN + "')";
    public static final String BOOKING_WRITE = "hasRole('" + TERMIN_ORGANISATOR + "')";
    public static final String BOOKING_READ = "hasRole('" + LESEBERECHTIGT + "')";
    public static final String BOOKING_SELF = "hasRole('" + ANWENDER + "')";
    public static final String APPOINTMENT_WRITE = "hasRole('" + ANWENDER + "')";

    private Authorities() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
