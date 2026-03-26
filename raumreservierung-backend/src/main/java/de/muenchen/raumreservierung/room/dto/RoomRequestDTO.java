package de.muenchen.raumreservierung.room.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record RoomRequestDTO(@NotNull @Size(min = 2, max = 100) String name, @NotNull @Size(max = 10) String number,
        @Size(max = 255) String address, int capacity, @Size(max = 1000) String information,
        @Size(max = 1000) String note, Boolean availability, int area,
        List<SeatingCapacityRequestDTO> roomSeatingCapacities,
        Set<UUID> equipmentIds) {
    public RoomRequestDTO {
        roomSeatingCapacities = roomSeatingCapacities == null ? List.of() : List.copyOf(roomSeatingCapacities);
        equipmentIds = equipmentIds == null ? Set.of() : Set.copyOf(equipmentIds);
    }
}
