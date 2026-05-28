package de.muenchen.raumreservierung.booking.dto;

import java.time.LocalDate;
import java.util.UUID;

public record BookingFilterDTO(
        UUID roomId,
        LocalDate start,
        LocalDate end
//TODO: add status filter
) {
}
