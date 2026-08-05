package de.muenchen.raumreservierung.appointment.dto;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.booking.dto.BookingMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = { BookingMapper.class })
public interface AppointmentMapper {
    @Mapping(source = "booking.id", target = "bookingId")
    AppointmentResponseDTO toDto(Appointment appointment);

    @Mapping(source = "booking", target = "bookingMinimal")
    AppointmentDetailsResponseDTO toDetailsDto(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    Appointment toEntity(AppointmentRequestDTO appointmentRequestDTO);
}
