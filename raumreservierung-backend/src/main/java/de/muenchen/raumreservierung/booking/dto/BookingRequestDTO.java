package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.types.BookingServiceTime;
import de.muenchen.raumreservierung.booking.types.BookingStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record BookingRequestDTO(
        @NotNull BookingStatus bookingStatus,
        @NotNull @Size(min = 2, max = 255) String title,
        int participantCount,
        List<UUID> equipmentUUIDs,
        @Size(max = 500) String specialSeatingRequest,
        boolean cateringNeeded,
        @Size(max = 500) String cateringCoordination,
        @Size(max = 10) List<BookingServiceTime> serviceTimes,
        @Size(max = 2000) String internalNotes) {
}
