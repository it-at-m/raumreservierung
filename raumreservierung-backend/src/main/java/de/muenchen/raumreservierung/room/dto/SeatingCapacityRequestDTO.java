package de.muenchen.raumreservierung.room.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SeatingCapacityRequestDTO(@NotNull UUID seatingTypeId, @NotNull int capacity) {
}
