package de.muenchen.raumreservierung.room.dto;

import de.muenchen.raumreservierung.equipment.dto.EquipmentResponseDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record RoomDetailsResponseDTO(
        UUID id,
        @NotNull @Size(min = 2, max = 100) String name,
        @NotNull @Size(max = 10) String number,
        @NotNull @Size(max = 255) String address,
        @NotNull int capacity, @NotNull @Size(max = 1000) String information,
        @NotNull @Size(max = 1000) String note, @NotNull Boolean availability,
        @NotNull int area, @NotNull List<SeatingCapacityResponseDTO> roomSeatingCapacities,
        @NotNull Set<EquipmentResponseDto> equipment) {
    public RoomDetailsResponseDTO {
        roomSeatingCapacities = roomSeatingCapacities == null ? List.of() : List.copyOf(roomSeatingCapacities);
        equipment = equipment == null ? Set.of() : Set.copyOf(equipment);
    }
}
