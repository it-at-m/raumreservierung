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
    @Mapping(target = "hasNote", source = "booking", qualifiedByName = "hasNoteCheck")
    @Mapping(target = "isRecurring", source = "booking", qualifiedByName = "isRecurringCheck")
    BookingListResponseDTO toListDto(Booking booking);

    @Mapping(target = "roomId", source = "room.id")
    BookingMinimalResponseDTO toMinimalDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookedFor", source = "bookedForId")
    @Mapping(target = "equipment", source = "equipmentIds")
    @Mapping(target = "room", source = "roomId")
    @Mapping(target = "seatingType", source = "seatingTypeId")
    Booking toEntity(BookingRequestDTO bookingRequestDTO);

    @Named("hasEquipmentCheck")
    default boolean mapHasEquipment(final Booking booking) {
        return !booking.getEquipment().isEmpty();
    }

    @Named("hasNoteCheck")
    default boolean mapHasNote(final Booking booking) {
        return (booking.getAdditionalNotes() != null && !booking.getAdditionalNotes().isEmpty())
                || (booking.getInternalNotes() != null && !booking.getInternalNotes().isEmpty());
    }

    @Named("isRecurringCheck")
    default boolean mapIsRecurring(final Booking booking) {
        return booking.getAppointments().size() > 1;
    }
}
