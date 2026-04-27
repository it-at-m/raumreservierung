package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.types.status.BookingStatus;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.room.dto.RoomListResponseDTO;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookingListResponseDTO(
        @NotNull UUID bookingId,
        @NotNull BookingStatus bookingStatus,
        @NotNull String title,
        @NotNull int participantCount,
        RoomListResponseDTO room,
        @NotNull List<Equipment> equipment,
        @NotNull LocalDateTime start,
        @NotNull LocalDateTime end) {
}
