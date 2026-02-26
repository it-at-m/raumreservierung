package de.muenchen.raumreservierung.security;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Each possible authority in this project is represented by a constant in this class.
 * The constants are used within the {@link org.springframework.stereotype.Controller} or
 * {@link org.springframework.stereotype.Service} classes in the method security annotations
 * (e.g. {@link PreAuthorize}).
 */
@SuppressWarnings({ "PMD.DataClass", "PMD.AvoidDuplicateLiterals" })
public final class Authorities {
    public static final String THEENTITY_GET = "hasAnyRole('anwender', 'fachadmin')";
    public static final String THEENTITY_GET_ALL = "hasAnyRole('anwender', 'fachadmin')";
    public static final String THEENTITY_CREATE = "hasAnyRole('fachadmin')";
    public static final String THEENTITY_UPDATE = "hasAnyRole('fachadmin')";
    public static final String THEENTITY_DELETE = "hasAnyRole('fachadmin')";
    public static final String EQUIPMENT_MANAGE = "hasAnyRole('fachadmin')";
    public static final String HOLIDAYS_MANAGE =  "hasAnyRole('fachadmin')";

    // Permissions based auth
    // public static final String THEENTITY_GET = "hasAuthority('REFARCH_THEENTITY_READ')";
    // public static final String THEENTITY_GET_ALL = "hasAuthority('REFARCH_THEENTITY_READ')";
    // public static final String THEENTITY_CREATE = "hasAuthority('REFARCH_THEENTITY_WRITE')";
    // public static final String THEENTITY_UPDATE = "hasAuthority('REFARCH_THEENTITY_WRITE')";
    // public static final String THEENTITY_DELETE = "hasAuthority('REFARCH_THEENTITY_DELETE')";

    private Authorities() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
