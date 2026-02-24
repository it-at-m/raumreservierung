package de.muenchen.raumreservierung.seating.dto;

import de.muenchen.raumreservierung.seating.SeatingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SeatingTypeMapper {

    SeatingResponseDto toDto(SeatingType seatingType);

    @Mapping(target = "id", ignore = true)
    SeatingType toEntity(SeatingRequestDto seatingRequestDto);
}
