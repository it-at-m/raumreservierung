package de.muenchen.raumreservierung.seating.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SeatingRequestDto(@NotNull @Size(min = 2, max = 100) String name, String description) {
}
