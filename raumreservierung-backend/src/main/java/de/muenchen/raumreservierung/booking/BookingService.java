package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_UNAUTHORIZED_ACTION;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.appointment.AppointmentService;
import de.muenchen.raumreservierung.booking.dto.BookingFilterDTO;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.common.UnauthorizedActionException;
import de.muenchen.raumreservierung.security.Authorities;
import de.muenchen.raumreservierung.security.Roles;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final AppointmentService appointmentService;

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking getById(final UUID bookingId) {
        final Booking booking = getEntityOrThrowException(bookingId);
        validateBookingAuthority(booking, Roles.LESEBERECHTIGT);
        return booking;
    }

    @PreAuthorize(Authorities.BOOKING_READ)
    public Page<Booking> getAllBookingsByPageableAndFilter(final Pageable pageable, final BookingFilterDTO bookingFilterDto) {
        final Specification<Booking> bookingSpecification = BookingSpecificationBuilder.fromFilter(bookingFilterDto);
        final Page<Booking> allBookings = bookingRepository.findAll(bookingSpecification, pageable);
        log.debug("Found {} bookings", allBookings.getTotalElements());
        return allBookings;
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Page<Booking> getOwnBookingsByPageableAndFilter(final Pageable pageable, final BookingFilterDTO bookingFilterDto) {
        final String email = getUserEmail();
        final List<Booking> ownBookings = bookingRepository.findByContactPersonEmail(email);
        log.debug("Found {} bookings", ownBookings.size());
        return null;
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking createBooking(final Booking booking) {
        if (lacksAuthority(Roles.TERMIN_ORGANISATOR)) {
            booking.setInternalNotes(null);
        }
        final Set<Appointment> calculatedAppointments = appointmentService.generateAndLinkAppointments(booking);
        booking.setAppointments(calculatedAppointments);

        final Booking savedBooking = saveAndDetach(new Booking(), booking);

        log.debug("Created booking with id {}", savedBooking.getId());
        return getEntityOrThrowException(savedBooking.getId());
    }

    /**
     * Updates an existing booking. If the recurrence rule is modified, it regenerates
     * future appointments while preserving the history of past appointments.
     *
     * @param bookingUpdates The booking object containing the updated data.
     * @param bookingId The unique identifier of the booking to be updated.
     * @return The updated and persisted booking entity.
     * @throws NotFoundException if no booking with the given ID exists.
     * @throws UnauthorizedActionException if the user is neither the owner nor an admin.
     */
    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking updateBooking(final Booking bookingUpdates, final UUID bookingId) {
        final Booking existingBooking = getEntityOrThrowException(bookingId);
        validateBookingAuthority(existingBooking, Roles.TERMIN_ORGANISATOR);

        if (lacksAuthority(Roles.TERMIN_ORGANISATOR)) {
            bookingUpdates.setInternalNotes(existingBooking.getInternalNotes());
        }

        if (!Objects.equals(bookingUpdates.getRecurringRule(), existingBooking.getRecurringRule())) {
            final Set<Appointment> newAppointments = appointmentService.generateAndLinkAppointments(bookingUpdates);

            final Set<Appointment> pastAppointments = existingBooking.getAppointments().stream()
                    .filter(a -> a.getSchedule().occupancyStart().isBefore(LocalDateTime.now()))
                    .collect(Collectors.toSet());

            final Set<Appointment> futureNewAppointments = newAppointments.stream()
                    .filter(a -> a.getSchedule().occupancyStart().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toSet());

            pastAppointments.addAll(futureNewAppointments);
            bookingUpdates.setAppointments(pastAppointments);
        }

        saveAndDetach(existingBooking, bookingUpdates);

        log.debug("Updated booking with id {}", existingBooking.getId());
        return getEntityOrThrowException(existingBooking.getId());
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public void deleteBooking(final UUID bookingId) {
        final Booking existingBooking = getEntityOrThrowException(bookingId);

        validateBookingAuthority(existingBooking, Roles.TERMIN_ORGANISATOR);
        log.debug("Deleted booking with id {}", bookingId);
        bookingRepository.deleteById(bookingId);
    }

    private Booking getEntityOrThrowException(final UUID bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, bookingId)));
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

    /**
     * Extracts the email address from the current security context.
     *
     * @return The email claim from the JWT if authenticated, otherwise null.
     */
    public String getUserEmail() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = null;
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            email = jwt.getClaimAsString("email");
        }
        return email;
    }

    /**
     * Checks if the currently authenticated user lacks a specific authority.
     * This method takes the configured role hierarchy into account.
     *
     * @param role The name of the authority/role to check for.
     * @return true if the user is not authenticated or does not have the authority.
     */
    public boolean lacksAuthority(final String role) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth == null || roleHierarchy.getReachableGrantedAuthorities(auth.getAuthorities())
                .stream()
                .noneMatch(a -> a.getAuthority().equals(role));
    }

    /**
     * Validates if the current user has permission to access a specific booking.
     * Access is granted if the user is the contact person for the booking
     * OR possesses the required administrative authority.
     *
     * @param booking The booking object to check access for.
     * @param role The administrative authority name that bypasses ownership checks.
     * @throws UnauthorizedActionException if the user is neither the owner nor an admin.
     */
    //rename
    public void validateBookingAuthority(final Booking booking, final String role) {
        final String email = getUserEmail();
        if (lacksAuthority(role) && !booking.getContactPerson().getEmail().equals(email)) {
            throw new UnauthorizedActionException(MSG_UNAUTHORIZED_ACTION);
        }
    }
}
