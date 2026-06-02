package de.muenchen.raumreservierung.appointment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentFilterDTO(
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        UUID bookingId,
        @NotNull UUID roomId) {
}
