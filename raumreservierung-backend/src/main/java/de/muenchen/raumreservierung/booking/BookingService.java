package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.security.Authorities;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;
    private final RoleHierarchy roleHierarchy;

    //check if user is authorized to get booking, i.e. if own booking or appropriate role
    public Booking getById(final UUID bookingId) {
        return getEntityOrThrowException(bookingId);
    }

    @PreAuthorize(Authorities.BOOKING_READ)
    public List<Booking> getAllBookings() {
        final List<Booking> allBookings = bookingRepository.findAll();
        log.debug("Found {} bookings", allBookings.size());
        return allBookings;
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public List<Booking> getOwnBookings() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = null;
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            email = jwt.getClaimAsString("email");
        }
        final List<Booking> ownBookings = bookingRepository.findByContactPersonEmail(email);
        log.debug("Found {} bookings", ownBookings.size());
        return ownBookings;
    }

    private boolean hasAuthority(final String authorityName) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth != null && roleHierarchy.getReachableGrantedAuthorities(auth.getAuthorities())
                .stream()
                .anyMatch(a -> a.getAuthority().equals(authorityName));
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking createBooking(final Booking booking) {
        if (!hasAuthority(Authorities.TERMIN_ORGANISATOR)) {
            booking.setInternalNotes(null);
        }

        final Booking newBooking = new Booking();
        newBooking.updateFrom(booking);

        final Booking savedBooking = bookingRepository.saveAndFlush(newBooking);
        entityManager.detach(savedBooking);
        if (savedBooking.getContactPerson() != null) {
            entityManager.detach(savedBooking.getContactPerson());
        }

        log.debug("Created booking with id {}", savedBooking.getId());
        return getEntityOrThrowException(savedBooking.getId());
    }

    //check if user is authorized to update booking, i.e. if own booking or appropriate role
    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking updateBooking(final Booking bookingUpdates, final UUID bookingId) {
        final Booking existingBooking = getEntityOrThrowException(bookingId);

        if (!hasAuthority(Authorities.TERMIN_ORGANISATOR)) {
            bookingUpdates.setInternalNotes(existingBooking.getInternalNotes());
        }
        existingBooking.updateFrom(bookingUpdates);
        bookingRepository.saveAndFlush(existingBooking);
        entityManager.detach(existingBooking);
        if (existingBooking.getContactPerson() != null) {
            entityManager.detach(existingBooking.getContactPerson());
        }

        log.debug("Updated booking with id {}", existingBooking.getId());
        return getEntityOrThrowException(existingBooking.getId());
    }

    @PreAuthorize(Authorities.BOOKING_WRITE)
    public void deleteBooking(final UUID bookingId) {
        log.debug("Deleted booking with id {}", bookingId);
        bookingRepository.deleteById(bookingId);
    }

    private Booking getEntityOrThrowException(final UUID bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, bookingId)));
    }
}
