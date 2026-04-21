package de.muenchen.raumreservierung.booking.types.dto;

import de.muenchen.raumreservierung.booking.types.BookingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BookingTypeMapper {

    @Mapping(source = "blocking", target = "isBlocking")
    BookingTypeDTO toDto(BookingType bookingType);

    @Mapping(source = "isBlocking", target = "blocking")
    BookingType toEntity(BookingTypeDTO bookingTypeDTO);
}
