package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.common.ReferenceMapper;
import de.muenchen.raumreservierung.person.dto.PersonMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = { ReferenceMapper.class, PersonMapper.class })
public interface BookingMapper {
    BookingDetailResponseDTO toDetailDto(Booking booking);

    @Mapping(target = "hasEquipment", expression = "java(!booking.getEquipment().isEmpty())")
    @Mapping(target = "isRecurring", expression = "java(booking.getAppointments().size() > 1)")
    BookingListResponseDTO toListDto(Booking booking);

    BookingMinimalResponseDTO toMinimalDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactPerson", source = "contactPersonId")
    Booking toEntity(BookingRequestDTO bookingRequestDTO);
}
