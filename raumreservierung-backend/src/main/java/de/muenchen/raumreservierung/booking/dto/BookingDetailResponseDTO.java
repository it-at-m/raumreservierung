package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.appointment.dto.AppointmentWithoutBookingIdResponseDTO;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.equipment.dto.EquipmentResponseDto;
import de.muenchen.raumreservierung.person.dto.PersonResponseDto;
import de.muenchen.raumreservierung.room.dto.RoomListResponseDTO;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record BookingDetailResponseDTO(
        @NotNull UUID id,
        @NotNull String title,
        @NotNull int participantCount,
        List<EquipmentResponseDto> equipments,
        @NotNull boolean cateringNeeded,
        String internalNotes,
        String additionalNotes,
        @NotNull List<AppointmentWithoutBookingIdResponseDTO> appointments,
        RoomListResponseDTO room,
        @NotNull ScheduleTemplate schedule,
        @NotNull PersonResponseDto contactPerson) {
}
