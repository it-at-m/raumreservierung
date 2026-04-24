package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.booking.dto.BookingMapper;
import de.muenchen.raumreservierung.booking.dto.BookingRequestDTO;
import de.muenchen.raumreservierung.booking.dto.BookingResponseDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    private final BookingMapper bookingMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDTO> getAllBookings() {
        return bookingService.findAll().stream().map(bookingMapper::toDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDTO createBooking(@Valid @RequestBody final BookingRequestDTO bookingRequestDTO) {
        return bookingMapper.toDto(bookingService.createBooking(bookingMapper.toEntity(bookingRequestDTO)));
    }

    @PutMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDTO updateBooking(@Valid @RequestBody final BookingRequestDTO bookingRequestDTO,
            @PathVariable("bookingId") final UUID bookingId) {
        return bookingMapper.toDto(bookingService.updateBooking(bookingMapper.toEntity(bookingRequestDTO), bookingId));
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBooking(@Valid @PathVariable("bookingId") final UUID bookingId) {
        bookingService.deleteBooking(bookingId);
    }
}
