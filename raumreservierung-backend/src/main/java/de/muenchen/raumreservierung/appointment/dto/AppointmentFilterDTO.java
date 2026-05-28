package de.muenchen.raumreservierung.appointment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record AppointmentFilterDTO(
        LocalDate startDate,
        LocalDate endDate,
        @NotNull UUID roomId) {
}
