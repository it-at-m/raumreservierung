package de.muenchen.raumreservierung.booking.dto;

import java.time.LocalDateTime;

public record BookingFilterDTO(
        String roomName,
        LocalDateTime start,
        LocalDateTime end
//TODO: add status filter
) {
}
