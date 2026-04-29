package de.muenchen.raumreservierung.appointment.dto;

import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AppointmentResponseDTO(
        @NotNull UUID id,
        @NotNull UUID bookingId,
        @NotNull ScheduleTemplate schedule) {
}
