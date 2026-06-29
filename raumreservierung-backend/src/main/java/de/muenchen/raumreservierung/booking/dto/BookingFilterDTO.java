package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.BookingStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingFilterDTO(
        UUID roomId,
        OffsetDateTime start,
        OffsetDateTime end,
        BookingStatus status) {
}
