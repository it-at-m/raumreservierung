package de.muenchen.raumreservierung.room.dto;

import de.muenchen.raumreservierung.equipment.dto.EquipmentResponseDto;
import de.muenchen.raumreservierung.person.dto.PersonResponseDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record RoomDetailsResponseDTO(
        UUID id,
        @NotNull @Size(min = 2, max = 100) String name,
        @NotNull @Size(max = 10) String number,
        @NotNull @Size(max = 255) String location,
        @NotNull @Size(max = 500) String locationDescription,
        @NotNull int capacity,
        @NotNull boolean isActive,
        @NotNull int area,
        @NotNull Set<SeatingCapacityResponseDTO> roomSeatingCapacities,
        @NotNull Set<EquipmentResponseDto> equipment,
        PersonResponseDto contactPerson) {
    public RoomDetailsResponseDTO {
        roomSeatingCapacities = roomSeatingCapacities == null ? Set.of() : Set.copyOf(roomSeatingCapacities);
        equipment = equipment == null ? Set.of() : Set.copyOf(equipment);
    }
}
