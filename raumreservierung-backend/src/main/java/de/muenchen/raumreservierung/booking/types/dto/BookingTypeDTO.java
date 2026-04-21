package de.muenchen.raumreservierung.booking.types.dto;

import de.muenchen.raumreservierung.booking.types.BookingStatus;
import jakarta.validation.constraints.NotNull;

public record BookingTypeDTO(
        @NotNull BookingStatus bookingStatus,
        @NotNull String color,
        @NotNull boolean isBlocking) {
}
