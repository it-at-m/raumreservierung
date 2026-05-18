package de.muenchen.raumreservierung.appointment.dto;

import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import jakarta.validation.constraints.NotNull;

public record AppointmentRequestDTO(
        @NotNull ScheduleTemplate schedule) {
}
