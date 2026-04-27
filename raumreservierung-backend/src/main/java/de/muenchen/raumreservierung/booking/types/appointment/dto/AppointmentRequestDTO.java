package de.muenchen.raumreservierung.booking.types.appointment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AppointmentRequestDTO(
        @NotNull LocalDateTime start,
        @NotNull LocalDateTime end,
        int duration) {
}
