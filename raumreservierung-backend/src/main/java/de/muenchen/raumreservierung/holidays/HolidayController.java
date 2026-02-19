package de.muenchen.raumreservierung.holidays;

import de.muenchen.raumreservierung.holidays.dto.HolidayMapper;
import de.muenchen.raumreservierung.holidays.dto.HolidayRequestDTO;
import de.muenchen.raumreservierung.holidays.dto.HolidayResponseDTO;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/holidays")
public class HolidayController {
    private final HolidayService holidayService;
    private final HolidayMapper holidayMapper;

    @GetMapping("/public")
    public List<HolidayResponseDTO> getPublicHolidays() {
        final List<Holiday> holidays = holidayService.getPublicHolidays();
        return holidays.stream().map(holidayMapper::toDTO).toList();
    }

    @GetMapping("/school")
    public List<HolidayResponseDTO> getSchoolHolidays() {
        final List<Holiday> holidays = holidayService.getSchoolHolidays();
        return holidays.stream().map(holidayMapper::toDTO).toList();
    }

    @PostMapping()
    public HolidayResponseDTO createHoliday(@RequestBody HolidayRequestDTO requestDTO) {
        return holidayMapper.toDTO(holidayService.createHoliday(holidayMapper.toEntity(requestDTO)));
    }

    @PutMapping()
    public HolidayResponseDTO updateHoliday(@RequestBody HolidayRequestDTO requestDTO) {
        return holidayMapper.toDTO(holidayService.updateHoliday(holidayMapper.toEntity(requestDTO)));
    }

    @DeleteMapping("/{id}")
    public void deleteHoliday(@PathVariable("id") final UUID holidayId) {
        holidayService.deleteHoliday(holidayId);
    }


}
