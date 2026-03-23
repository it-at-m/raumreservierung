package de.muenchen.raumreservierung.equipment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EquipmentRequestDto(@NotNull @Size(min = 2, max = 100) String name, String description,
        @NotNull boolean isActive) {
}
