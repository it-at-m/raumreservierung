package de.muenchen.raumreservierung.room.dto;

import de.muenchen.raumreservierung.equipment.dto.EquipmentMapper;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.seating.dto.SeatingTypeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {EquipmentMapper.class, SeatingTypeMapper.class})
public interface RoomMapper {

    RoomResponseDTO toDTO(Room room);

    @Mapping(target = "id", ignore = true)
    Room toEntity(RoomRequestDTO roomRequestDTO);


}
