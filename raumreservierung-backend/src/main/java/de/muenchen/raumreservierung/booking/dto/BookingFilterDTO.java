package de.muenchen.raumreservierung.booking.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingFilterDTO(
        UUID roomId,
        LocalDateTime start,
        LocalDateTime end
//TODO: add status filter
) {
}
