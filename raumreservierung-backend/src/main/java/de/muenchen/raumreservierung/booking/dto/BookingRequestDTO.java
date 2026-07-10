package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.BookingType;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import org.hibernate.validator.constraints.Range;

@SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "DTOs are simple data carriers")
public record BookingRequestDTO(
        @NotNull @Size(min = 2, max = 255) String title,
        @Range(min = 0, max = 99_999) int participantCount,
        List<UUID> equipmentIds,
        boolean cateringNeeded,
        @Size(max = 500) String internalNotes,
        @Size(max = 500) String additionalNotes,
        String recurringRule,
        UUID roomId,
        @NotNull ScheduleTemplate schedule,
        UUID bookedForId,
        UUID seatingTypeId,
        BookingType bookingType
//TODO: add status
) {
}
