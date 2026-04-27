package de.muenchen.raumreservierung.booking.types.appointment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponseDTO(
        UUID appointmentId,
        @NotNull LocalDateTime start,
        @NotNull LocalDateTime end,
        int duration) {
}
