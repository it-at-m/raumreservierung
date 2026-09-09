package de.muenchen.raumreservierung.appointment.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@SuppressFBWarnings(value = {"EI_EXPOSE_REP"}, justification = "DTOs are simple data carriers")
public record AppointmentFilterDTO(
        @NotNull OffsetDateTime startDate,
        @NotNull OffsetDateTime endDate,
        UUID bookingId,
        @Parameter(hidden = true)
        List<UUID> roomIds) {
}
