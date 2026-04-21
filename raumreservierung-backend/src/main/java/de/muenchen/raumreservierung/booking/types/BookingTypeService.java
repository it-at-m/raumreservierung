package de.muenchen.raumreservierung.booking.types;

import de.muenchen.raumreservierung.security.Authorities;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingTypeService {

    private final BookingTypeRepository bookingTypeRepository;

    public List<BookingType> findAll() {
        final List<BookingType> allBookingTypes = bookingTypeRepository.findAll();
        log.debug("Found {} equipments", allBookingTypes.size());
        return allBookingTypes;
    }

    @PreAuthorize(Authorities.BOOKING_TYPES_MANAGE)
    public BookingType createOrUpdate(final BookingType incoming) {
        return bookingTypeRepository.findByBookingStatus(incoming.getBookingStatus())
                .map(existing -> {
                    existing.updateFrom(incoming);
                    return bookingTypeRepository.save(existing);
                })
                .orElseGet(() -> bookingTypeRepository.save(incoming));
    }

}
