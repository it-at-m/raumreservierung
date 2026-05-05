package de.muenchen.raumreservierung.appointment.dto;

import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AppointmentExistingBookingRequestDTO(
        @NotNull ScheduleTemplate schedule,
        @NotNull UUID bookingId) {
}
