package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.appointment.dto.AppointmentRequestDTO;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.room.dto.RoomRequestDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record BookingRequestDTO(
        @NotNull @Size(min = 2, max = 255) String title,
        int participantCount,
        List<UUID> equipmentUUIDs,
        boolean cateringNeeded,
        @Size(max = 500) String internalNotes,
        @Size(max = 500) String additionalNotes,
        List<AppointmentRequestDTO> appointments,
        RoomRequestDTO room,
        @NotNull ScheduleTemplate schedule,
        UUID contactPersonId) {
}
