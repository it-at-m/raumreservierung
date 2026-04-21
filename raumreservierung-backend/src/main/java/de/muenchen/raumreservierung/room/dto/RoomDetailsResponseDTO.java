package de.muenchen.raumreservierung.room.dto;

import de.muenchen.raumreservierung.equipment.dto.EquipmentResponseDto;
import de.muenchen.raumreservierung.person.dto.PersonResponseDto;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

@SuppressFBWarnings(value = {"EI_EXPOSE_REP"}, justification = "DTOs are simple data carriers")
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
}
