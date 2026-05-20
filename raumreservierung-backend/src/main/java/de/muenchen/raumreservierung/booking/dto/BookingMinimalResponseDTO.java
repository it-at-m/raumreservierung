package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.person.dto.PersonResponseDto;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BookingMinimalResponseDTO(
        @NotNull UUID id,
        @NotNull String title,
        @NotNull PersonResponseDto bookedBy,
        PersonResponseDto bookedFor) {
}
