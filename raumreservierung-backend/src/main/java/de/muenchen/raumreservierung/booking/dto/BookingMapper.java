package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.common.ReferenceMapper;
import de.muenchen.raumreservierung.equipment.dto.EquipmentMapper;
import de.muenchen.raumreservierung.person.dto.PersonMapper;
import de.muenchen.raumreservierung.room.dto.RoomMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = { ReferenceMapper.class, EquipmentMapper.class, PersonMapper.class, RoomMapper.class })
public interface BookingMapper {
    @Mapping(target = "equipments", source = "equipment")
    BookingDetailResponseDTO toDetailDto(Booking booking);

    @Mapping(target = "hasEquipment", source = "booking", qualifiedByName = "hasEquipmentCheck")
    @Mapping(target = "isRecurring", source = "booking", qualifiedByName = "isRecurringCheck")
    BookingListResponseDTO toListDto(Booking booking);

    BookingMinimalResponseDTO toMinimalDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookedFor", source = "bookedForId")
    @Mapping(target = "equipment", source = "equipmentIds")
    @Mapping(target = "room", source = "roomId")
    Booking toEntity(BookingRequestDTO bookingRequestDTO);

    @Named("hasEquipmentCheck")
    default boolean mapHasEquipment(final Booking booking) {
        return booking.getEquipment().isEmpty();
    }

    @Named("isRecurringCheck")
    default boolean mapIsRecurring(final Booking booking) {
        return booking.getAppointments().size() > 1;
    }
}
