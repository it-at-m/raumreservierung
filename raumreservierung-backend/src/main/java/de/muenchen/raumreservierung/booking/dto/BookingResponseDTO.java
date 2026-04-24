package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.types.BookingServiceTime;
import de.muenchen.raumreservierung.booking.types.BookingStatus;
import de.muenchen.raumreservierung.equipment.Equipment;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BookingResponseDTO(
        @NotNull BookingStatus bookingStatus,
        @NotNull String title,
        int participantCount,
        List<Equipment> equipments,
        String specialSeatingRequest,
        boolean cateringNeeded,
        String cateringCoordination,
        String internalNotes,
        List<BookingServiceTime> serviceTimes) {
}
