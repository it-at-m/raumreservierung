package de.muenchen.raumreservierung.room.dto;

import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.dto.EquipmentMapper;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.seating.SeatingType;
import de.muenchen.raumreservierung.seating.dto.SeatingTypeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(uses = {EquipmentMapper.class, SeatingTypeMapper.class})
public interface RoomMapper {

    RoomDetailsResponseDTO toDetailsDto(Room room);

    RoomResponseDTO toDTO(Room room);

    @Mapping(target = "id", ignore = true)
    Room toEntity(RoomRequestDTO roomRequestDTO);

    default Equipment mapIdToEquipment(final UUID id) {
        final Equipment equipment = new Equipment();
        equipment.setId(id);
        return equipment;
    }

    default SeatingType mapIdToSeatingType(final UUID id) {
        final SeatingType seatingType = new SeatingType();
        seatingType.setId(id);
        return seatingType;
    }

}
