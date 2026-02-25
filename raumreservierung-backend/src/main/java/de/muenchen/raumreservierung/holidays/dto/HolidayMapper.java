package de.muenchen.raumreservierung.holidays.dto;

import de.muenchen.raumreservierung.holidays.Holiday;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper
public interface HolidayMapper {

    HolidayResponseDTO toDTO(Holiday h);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "dateMapper")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "dateMapper")
    Holiday toEntity(HolidayRequestDTO dto);

    @Named("dateMapper")
    default LocalDate toLocalDate(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Europe/Berlin")).toLocalDate();
    }
}
