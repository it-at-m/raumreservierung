package de.muenchen.raumreservierung.room.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "DTOs are simple data carriers")
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

}
