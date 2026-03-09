package de.muenchen.raumreservierung.holidays;

import de.muenchen.raumreservierung.holidays.dto.HolidayMapper;
import de.muenchen.raumreservierung.holidays.dto.HolidayRequestDTO;
import de.muenchen.raumreservierung.holidays.dto.HolidayResponseDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/holidays")
public class HolidayController {
    private final HolidayService holidayService;
    private final HolidayMapper holidayMapper;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<HolidayResponseDTO> getHolidays(@RequestParam final boolean isPublic) {
        final List<Holiday> holidays = holidayService.getHolidays(isPublic);
        return holidays.stream().map(holidayMapper::toDTO).toList();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public HolidayResponseDTO createHoliday(@Valid @RequestBody final HolidayRequestDTO requestDTO) throws BadRequestException {
        return holidayMapper.toDTO(holidayService.createHoliday(holidayMapper.toEntity(requestDTO)));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public HolidayResponseDTO updateHoliday(@PathVariable("id") final UUID holidayId, @Valid @RequestBody final HolidayRequestDTO requestDTO) {
        return holidayMapper.toDTO(holidayService.updateHoliday(holidayMapper.toEntity(requestDTO), holidayId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteHoliday(@PathVariable("id") final UUID holidayId) {
        holidayService.deleteHoliday(holidayId);
    }

}
