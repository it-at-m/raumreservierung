package de.muenchen.raumreservierung.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("PMD.DataClass")
public class ExceptionMessageConstants {
    public static final String MSG_NOT_FOUND = "Could not find entity with id %s";
    public static final String MSG_CANNOT_DELETE_ACTIVE = "Cannot delete entity with id %s";
    public static final String MSG_START_DATE_AFTER_END_DATE = "Start date after end date";
    public static final String MSG_UNAUTHORIZED_ACTION = "Unauthorized action";
}
