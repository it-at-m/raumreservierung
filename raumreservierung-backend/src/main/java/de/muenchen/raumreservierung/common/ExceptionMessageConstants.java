package de.muenchen.raumreservierung.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("PMD.DataClass")
public class ExceptionMessageConstants {
    public static final String MSG_NOT_FOUND = "Could not find entity with id %s";
    public static final String MSG_NOT_FOUND_LDAP = "Could not find ldap entry with id %s";
    public static final String MSG_CANNOT_DELETE_ACTIVE = "Cannot delete entity with id %s";
    public static final String MSG_START_DATE_AFTER_END_DATE = "Start date after end date";
    public static final String MSG_UNAUTHORIZED_ACTION = "Unauthorized action";
    public static final String MSG_SEATINGTYPE_NOT_AVAILABLE = "Seating type not available in selected room or no room selected";
    public static final String MSG_ROOM_INACTIVE = "Booking of inactive rooms is not possible";
    public static final String MSG_FILE_READING_ERROR = "File processing error";
    public static final String MSG_PARTICIPANT_COUNT_INVALID = "Participant count is invalid";
    public static final String MSG_EQUIPMENT_INACTIVE = "Booking of inactive equipment is not possible";
    public static final String MSG_SEATINGTYPE_INACTIVE = "Booking of inactive seating type is not possible";
}
