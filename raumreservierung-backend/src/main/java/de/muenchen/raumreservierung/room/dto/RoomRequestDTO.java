package de.muenchen.raumreservierung.room.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record RoomRequestDTO(@NotNull @Size(min = 2, max = 100) String name,
        @NotNull @Size(max = 10) String number,
        @Size(max = 255) String location,
        @Size(max = 500) String locationDescription,
        int capacity,
        @NotNull boolean isActive,
        int area,
        Set<SeatingCapacityRequestDTO> roomSeatingCapacities,
        Set<UUID> equipmentIds,
        UUID contactPersonId) {

    public RoomRequestDTO {
        roomSeatingCapacities = roomSeatingCapacities == null ? Set.of() : Set.copyOf(roomSeatingCapacities);
        equipmentIds = equipmentIds == null ? Set.of() : Set.copyOf(equipmentIds);
    }
}
