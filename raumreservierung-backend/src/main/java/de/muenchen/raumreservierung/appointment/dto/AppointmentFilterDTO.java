package de.muenchen.raumreservierung.appointment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentFilterDTO(
        LocalDateTime startDate,
        LocalDateTime endDate,
        @NotNull UUID roomId) {
}
