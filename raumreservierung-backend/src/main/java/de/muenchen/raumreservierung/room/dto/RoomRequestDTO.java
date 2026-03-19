package de.muenchen.raumreservierung.room.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record RoomRequestDTO(@NotNull @Size(min = 2, max = 100) String name, @Size(max = 10) String number,
                             @Size(max = 255) String address, int capacity, @Size(max = 1000) String information,
                             @Size(max = 1000) String note, Boolean availability, int area, Set<UUID> seatingTypeIds,
                             Set<UUID> equipmentIds) {
}
