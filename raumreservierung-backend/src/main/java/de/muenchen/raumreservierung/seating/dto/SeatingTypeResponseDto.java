package de.muenchen.raumreservierung.seating.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SeatingTypeResponseDto(UUID id, @NotNull @Size(min = 2, max = 100) String name,
                                     @NotNull @Size(max = 255) String description,
                                     @NotNull boolean isActive) {
}
