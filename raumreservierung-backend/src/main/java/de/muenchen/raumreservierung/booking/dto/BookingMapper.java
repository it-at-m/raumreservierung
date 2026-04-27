package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BookingMapper {
    BookingResponseDTO toDto(Booking booking);

    BookingListResponseDTO toListDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    Booking toEntity(BookingRequestDTO bookingRequestDTO);
}
