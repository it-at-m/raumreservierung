package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_UNAUTHORIZED_ACTION;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.common.UnauthorizedActionException;
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

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking getById(final UUID bookingId) {
        final Booking booking = getEntityOrThrowException(bookingId);
        hasBookingAccessOrThrowException(booking, Authorities.LESEBERECHTIGT);
        return booking;
    }

    @PreAuthorize(Authorities.BOOKING_READ)
    public List<Booking> getAllBookings() {
        final List<Booking> allBookings = bookingRepository.findAll();
        log.debug("Found {} bookings", allBookings.size());
        return allBookings;
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public List<Booking> getOwnBookings() {
        final String email = getUserEmail();
        final List<Booking> ownBookings = bookingRepository.findByContactPersonEmail(email);
        log.debug("Found {} bookings", ownBookings.size());
        return ownBookings;
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking createBooking(final Booking booking) {
        if (lacksAuthority(Authorities.TERMIN_ORGANISATOR)) {
            booking.setInternalNotes(null);
        }

        final Booking savedBooking = saveAndDetach(new Booking(), booking);

        log.debug("Created booking with id {}", savedBooking.getId());
        return getEntityOrThrowException(savedBooking.getId());
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking updateBooking(final Booking bookingUpdates, final UUID bookingId) {
        final Booking existingBooking = getEntityOrThrowException(bookingId);
        hasBookingAccessOrThrowException(existingBooking, Authorities.TERMIN_ORGANISATOR);

        if (lacksAuthority(Authorities.TERMIN_ORGANISATOR)) {
            bookingUpdates.setInternalNotes(existingBooking.getInternalNotes());
        }

        saveAndDetach(existingBooking, bookingUpdates);

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

    private String getUserEmail() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = null;
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            email = jwt.getClaimAsString("email");
        }
        return email;
    }

    private boolean lacksAuthority(final String authorityName) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth == null || roleHierarchy.getReachableGrantedAuthorities(auth.getAuthorities())
                .stream()
                .noneMatch(a -> a.getAuthority().equals(authorityName));
    }

    private void hasBookingAccessOrThrowException(final Booking booking, final String authorityName) {
        final String email = getUserEmail();
        if (!booking.getContactPerson().getEmail().equals(email) && lacksAuthority(authorityName)) {
            throw new UnauthorizedActionException(MSG_UNAUTHORIZED_ACTION);
        }
    }

    private Booking saveAndDetach(final Booking bookingToUpdate, final Booking sourceData) {
        bookingToUpdate.updateFrom(sourceData);

        final Booking savedBooking = bookingRepository.saveAndFlush(bookingToUpdate);

        entityManager.detach(savedBooking);
        if (savedBooking.getContactPerson() != null) {
            entityManager.detach(savedBooking.getContactPerson());
        }

        return savedBooking;
    }
}
