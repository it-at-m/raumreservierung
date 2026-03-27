package de.muenchen.raumreservierung.room.dto;

import de.muenchen.raumreservierung.common.ReferenceMapper;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomSeatingCapacity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {ReferenceMapper.class}
)
public interface RoomMapper {

    RoomDetailsResponseDTO toDetailsDto(Room room);

    RoomListResponseDTO toDTO(Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "equipment", source = "equipmentIds")
    Room toEntity(RoomRequestDTO roomRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "seatingType", source = "seatingTypeId")
    RoomSeatingCapacity toSeatingCapacityEntity(SeatingCapacityRequestDTO dto);

}
