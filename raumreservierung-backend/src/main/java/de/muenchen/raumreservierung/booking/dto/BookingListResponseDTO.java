package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.person.dto.PersonResponseDto;
import de.muenchen.raumreservierung.room.dto.RoomListResponseDTO;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BookingListResponseDTO(
        @NotNull UUID id,
        @NotNull String title,
        int participantCount,
        RoomListResponseDTO room,
        //boolean -> annotation in mapper?
        @NotNull boolean hasEquipment,
        //boolean -> for recurring appointment
        @NotNull boolean isRecurring,
        //template + internal time as embeddable
        @NotNull ScheduleTemplate schedule,
        @NotNull PersonResponseDto contactPerson) {
}
