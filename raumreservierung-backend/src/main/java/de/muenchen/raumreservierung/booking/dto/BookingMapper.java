package de.muenchen.raumreservierung.booking.dto;

import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.common.ReferenceMapper;
import de.muenchen.raumreservierung.equipment.dto.EquipmentMapper;
import de.muenchen.raumreservierung.person.dto.PersonMapper;
import de.muenchen.raumreservierung.room.dto.RoomMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = { ReferenceMapper.class, EquipmentMapper.class, PersonMapper.class, RoomMapper.class })
public interface BookingMapper {
    @Mapping(target = "equipments", source = "equipment")
    BookingDetailResponseDTO toDetailDto(Booking booking);

    @Mapping(target = "hasEquipment", expression = "java(!booking.getEquipment().isEmpty())")
    @Mapping(target = "isRecurring", expression = "java(booking.getAppointments().size() > 1)")
    BookingListResponseDTO toListDto(Booking booking);

    BookingMinimalResponseDTO toMinimalDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactPerson", source = "contactPersonId")
    @Mapping(target = "equipment", source = "equipmentIds")
    @Mapping(target = "room", source = "roomId")
    Booking toEntity(BookingRequestDTO bookingRequestDTO);
}
