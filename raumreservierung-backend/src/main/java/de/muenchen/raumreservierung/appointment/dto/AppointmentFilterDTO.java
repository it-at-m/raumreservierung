package de.muenchen.raumreservierung.appointment.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AppointmentFilterDTO(
        LocalDate startDate,
        LocalDate endDate,
        UUID roomId) {
}
