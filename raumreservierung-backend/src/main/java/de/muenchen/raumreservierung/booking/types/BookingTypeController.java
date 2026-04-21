package de.muenchen.raumreservierung.booking.types;

import de.muenchen.raumreservierung.booking.types.dto.BookingTypeDTO;
import de.muenchen.raumreservierung.booking.types.dto.BookingTypeMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/booking/type")
public class BookingTypeController {

    private final BookingTypeService bookingTypeService;

    private final BookingTypeMapper bookingTypeMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingTypeDTO> getAllBookingTypes() {
        return bookingTypeService.findAll().stream().map(bookingTypeMapper::toDto).toList();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public BookingTypeDTO updateOrCreateBookingType(@Valid @RequestBody final BookingTypeDTO bookingType) {
        return bookingTypeMapper.toDto(bookingTypeService.createOrUpdate(bookingTypeMapper.toEntity(bookingType)));
    }
}
