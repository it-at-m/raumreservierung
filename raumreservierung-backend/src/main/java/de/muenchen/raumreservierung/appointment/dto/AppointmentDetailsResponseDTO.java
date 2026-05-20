package de.muenchen.raumreservierung.appointment.dto;

import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.booking.dto.BookingMinimalResponseDTO;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AppointmentDetailsResponseDTO(
        @NotNull UUID id,
        @NotNull ScheduleTemplate schedule,
        @NotNull BookingMinimalResponseDTO bookingMinimal) {
}
