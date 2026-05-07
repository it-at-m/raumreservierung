package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.booking.dto.BookingDetailResponseDTO;
import de.muenchen.raumreservierung.booking.dto.BookingListResponseDTO;
import de.muenchen.raumreservierung.booking.dto.BookingMapper;
import de.muenchen.raumreservierung.booking.dto.BookingRequestDTO;
import jakarta.transaction.Transactional;
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
    public List<BookingListResponseDTO> getAllBookings() {
        return bookingService.getAllBookings().stream().map(bookingMapper::toListDto).toList();
    }

    @GetMapping("/self")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingListResponseDTO> getOwnBookings() {
        return bookingService.getOwnBookings().stream().map(bookingMapper::toListDto).toList();
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDetailResponseDTO getBooking(@PathVariable final UUID bookingId) {
        return bookingMapper.toDetailDto(bookingService.getById(bookingId));
    }

    @Transactional
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDetailResponseDTO createBooking(@Valid @RequestBody final BookingRequestDTO bookingRequestDTO) {
        return bookingMapper.toDetailDto(bookingService.createBooking(bookingMapper.toEntity(bookingRequestDTO)));
    }

    @Transactional
    @PutMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDetailResponseDTO updateBooking(@Valid @RequestBody final BookingRequestDTO bookingRequestDTO,
            @PathVariable("bookingId") final UUID bookingId) {
        return bookingMapper.toDetailDto(bookingService.updateBooking(bookingMapper.toEntity(bookingRequestDTO), bookingId));
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBooking(@Valid @PathVariable("bookingId") final UUID bookingId) {
        bookingService.deleteBooking(bookingId);
    }
}
