package de.muenchen.raumreservierung.room.dto;

import de.muenchen.raumreservierung.seating.dto.SeatingTypeResponseDto;
import jakarta.validation.constraints.NotNull;

public record SeatingCapacityResponseDTO(@NotNull SeatingTypeResponseDto seatingType, @NotNull int capacity) {
}
