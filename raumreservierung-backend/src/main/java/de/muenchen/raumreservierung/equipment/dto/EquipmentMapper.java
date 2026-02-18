package de.muenchen.raumreservierung.equipment.dto;

import de.muenchen.raumreservierung.equipment.Equipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EquipmentMapper {

    EquipmentResponseDto toDto(Equipment equipment);

    @Mapping(target = "id", ignore = true)
    Equipment toEntity(EquipmentRequestDto equipmentRequestDto);
}
