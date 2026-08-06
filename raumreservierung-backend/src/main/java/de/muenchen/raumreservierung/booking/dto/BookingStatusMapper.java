package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.BookingTransitionService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = { BookingTransitionService.class })
public interface BookingStatusMapper {

    @Mapping(target = "currentStatus", source = "status")
    @Mapping(target = "nextPossibleStatus", source = "status")
    BookingStatusDTO toStatusDto(Booking booking);
}
