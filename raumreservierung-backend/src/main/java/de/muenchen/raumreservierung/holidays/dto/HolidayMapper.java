package de.muenchen.raumreservierung.holidays.dto;

import de.muenchen.raumreservierung.holidays.Holiday;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface HolidayMapper {

    HolidayResponseDTO toDTO(Holiday h);

    @Mapping(target = "id", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Holiday toEntity(HolidayRequestDTO dto);
}
