package de.muenchen.raumreservierung.booking.types.appointment.dto;

import de.muenchen.raumreservierung.booking.types.appointment.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AppointmentMapper {
    AppointmentResponseDTO toDto(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    Appointment toEntity(AppointmentRequestDTO appointmentRequestDTO);
}
