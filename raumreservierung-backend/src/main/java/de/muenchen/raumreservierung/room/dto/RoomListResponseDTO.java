package de.muenchen.raumreservierung.room.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record RoomListResponseDTO(UUID id,
        @NotNull @Size(min = 2, max = 100) String name,
        @NotNull @Size(max = 100) String number,
        @NotNull boolean isActive) {
}
