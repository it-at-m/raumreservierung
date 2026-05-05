package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.appointment.dto.AppointmentNewBookingRequestDTO;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record BookingRequestDTO(
        @NotNull @Size(min = 2, max = 255) String title,
        int participantCount,
        List<UUID> equipmentIds,
        boolean cateringNeeded,
        @Size(max = 500) String internalNotes,
        @Size(max = 500) String additionalNotes,
        List<AppointmentNewBookingRequestDTO> appointments,
        UUID roomId,
        @NotNull ScheduleTemplate schedule,
        UUID contactPersonId) {
}
