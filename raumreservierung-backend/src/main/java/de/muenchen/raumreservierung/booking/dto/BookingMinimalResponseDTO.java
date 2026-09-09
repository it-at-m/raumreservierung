package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.BookingStatus;
import de.muenchen.raumreservierung.person.dto.PersonResponseDto;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookingMinimalResponseDTO(
        @NotNull UUID id,
        @NotNull String title,
        @NotNull BookingStatus status,
        @NotNull PersonResponseDto bookedBy,
        @NotNull PersonResponseDto bookedFor,
        UUID roomId) {
}
