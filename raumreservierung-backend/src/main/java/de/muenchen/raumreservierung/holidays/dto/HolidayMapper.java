package de.muenchen.raumreservierung.holidays.dto;

import de.muenchen.raumreservierung.holidays.Holiday;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface HolidayMapper {

    HolidayResponseDTO toDTO(Holiday h);

    @Mapping(target = "id", ignore = true)
    Holiday toEntity(HolidayRequestDTO dto);
}
