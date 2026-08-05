package de.muenchen.raumreservierung.booking.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingFilterDTO(
        UUID roomId,
        OffsetDateTime start,
        OffsetDateTime end,
        UUID bookedForId,
        String title
//TODO: add status filter
) {
}
