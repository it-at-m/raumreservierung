package de.muenchen.raumreservierung.room.dto;

import de.muenchen.raumreservierung.equipment.dto.EquipmentResponseDto;
import de.muenchen.raumreservierung.seating.dto.SeatingTypeResponseDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record RoomResponseDTO(UUID id, @NotNull @Size(min = 2, max = 100) String name,
                              @NotNull @Size(max = 10) String number,
                              @NotNull @Size(max = 255) String address, @NotNull int capacity,
                              @NotNull @Size(max = 1000) String information,
                              @NotNull @Size(max = 1000) String note, @NotNull Boolean availability, @NotNull int area,
                              @NotNull Set<SeatingTypeResponseDto> seatingType,
                              @NotNull Set<EquipmentResponseDto> equipment) {

}
