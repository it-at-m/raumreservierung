package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.booking.types.BookingStatus;
import de.muenchen.raumreservierung.booking.types.BookingSubStatus;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.security.Authorities;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;

    public Booking getById(final UUID bookingId) {
        return getEntityOrThrowException(bookingId);
    }

    //differentiate between self and all in controller or here -> REST-Guidelines?
    public List<Booking> findAll() {
        final List<Booking> allBookings = bookingRepository.findAll();
        log.debug("Found {} bookings", allBookings.size());
        return allBookings;
    }

    @PreAuthorize(Authorities.BOOKING_MANAGE + " or " + Authorities.BOOKING_WRITE)
    public Booking createBooking(final Booking booking) {
        setBookingSubStatusToNew(booking);

        if (isOnlyBookingWriter()) {
            booking.setCateringCoordination(null);
            booking.setInternalNotes(null);
            booking.getServiceTimes().clear();
        }

        log.debug("Creating booking {}", booking);
        return bookingRepository.save(booking);
    }

    private boolean isOnlyBookingWriter() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals(Authorities.BOOKING_WRITE));
    }

    private void setBookingSubStatusToNew(final Booking booking) {
        BookingStatus status = booking.getBookingStatus();
        BookingStatus updatedStatus = new BookingStatus(status.bookingState(), BookingSubStatus.NEW);
        booking.setBookingStatus(updatedStatus);
    }

    @PreAuthorize(Authorities.BOOKING_MANAGE)
    public Booking updateBooking(final Booking bookingUpdates, final UUID bookingId) {
        final Booking existingBooking = getEntityOrThrowException(bookingId);

        if (isOnlyBookingWriter()) {
            bookingUpdates.setCateringCoordination(existingBooking.getCateringCoordination());
            bookingUpdates.setInternalNotes(existingBooking.getInternalNotes());
            bookingUpdates.setServiceTimes(existingBooking.getServiceTimes());
        }

        existingBooking.updateFrom(bookingUpdates);
        log.debug("Updated booking with id {}", existingBooking.getId());
        return bookingRepository.save(existingBooking);
    }

    @PreAuthorize(Authorities.BOOKING_MANAGE)
    public void deleteBooking(final UUID bookingId) {
        log.debug("Deleted booking with id {}", bookingId);
        bookingRepository.deleteById(bookingId);
    }

    private Booking getEntityOrThrowException(final UUID bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, bookingId)));
    }
}
