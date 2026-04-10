package de.muenchen.raumreservierung.room.dto;

import de.muenchen.raumreservierung.common.ReferenceMapper;
import de.muenchen.raumreservierung.equipment.dto.EquipmentMapper;
import de.muenchen.raumreservierung.person.dto.PersonMapper;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomSeatingCapacity;
import de.muenchen.raumreservierung.seating.dto.SeatingTypeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = { ReferenceMapper.class, SeatingTypeMapper.class, EquipmentMapper.class, PersonMapper.class }
)
public interface RoomMapper {
    @Mapping(source = "active", target = "isActive")
    RoomDetailsResponseDTO toDetailsDto(Room room);

    @Mapping(source = "active", target = "isActive")
    RoomListResponseDTO toDTO(Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "equipment", source = "equipmentIds")
    @Mapping(target = "contactPerson", source = "contactPersonId")
    @Mapping(source = "isActive", target = "active")
    Room toEntity(RoomRequestDTO roomRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "seatingType", source = "seatingTypeId")
    RoomSeatingCapacity toSeatingCapacityEntity(SeatingCapacityRequestDTO dto);

}
