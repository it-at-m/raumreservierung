package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "DTOs are simple data carriers")
public record BookingRequestDTO(
        @NotNull @Size(min = 2, max = 255) String title,
        int participantCount,
        List<UUID> equipmentIds,
        boolean cateringNeeded,
        @Size(max = 500) String internalNotes,
        @Size(max = 500) String additionalNotes,
        String recurringRule,
        UUID roomId,
        @NotNull ScheduleTemplate schedule,
        UUID contactPersonId
//TODO: add status
) {
}
