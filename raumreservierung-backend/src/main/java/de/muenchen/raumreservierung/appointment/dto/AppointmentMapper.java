package de.muenchen.raumreservierung.appointment.dto;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.booking.dto.BookingMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = { BookingMapper.class })
public interface AppointmentMapper {
    AppointmentResponseDTO toDto(Appointment appointment);

    @Mapping(source = "booking", target = "bookingMinimal")
    AppointmentSearchResponseDTO toSearchDto(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    Appointment toEntity(AppointmentExistingBookingRequestDTO appointmentExistingBookingRequestDTO);
}
