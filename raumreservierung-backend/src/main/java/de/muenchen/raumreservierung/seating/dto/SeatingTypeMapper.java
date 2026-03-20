package de.muenchen.raumreservierung.seating.dto;

import de.muenchen.raumreservierung.seating.SeatingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SeatingTypeMapper {

    @Mapping(source = "active", target = "isActive")
    SeatingTypeResponseDto toDto(SeatingType seatingType);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "isActive", target = "active")
    SeatingType toEntity(SeatingTypeRequestDto seatingTypeRequestDto);
}
