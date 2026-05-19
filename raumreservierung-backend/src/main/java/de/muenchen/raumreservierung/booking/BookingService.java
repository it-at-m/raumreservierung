package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_UNAUTHORIZED_ACTION;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.appointment.AppointmentService;
import de.muenchen.raumreservierung.booking.dto.BookingFilterDTO;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.common.UnauthorizedActionException;
import de.muenchen.raumreservierung.person.PersonService;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.security.AuthUtils;
import de.muenchen.raumreservierung.security.Authorities;
import de.muenchen.raumreservierung.security.Roles;
import de.muenchen.raumreservierung.security.SecurityContextService;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;
    private final SecurityContextService securityContextService;
    private final AppointmentService appointmentService;
    private final PersonService personService;

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking getById(final UUID bookingId) {
        final Booking booking = getEntityOrThrowException(bookingId);
        if (validateBookingAuthority(booking, Roles.LESEBERECHTIGT)) {
            return booking;
        } else {
            throw new UnauthorizedActionException(MSG_UNAUTHORIZED_ACTION);
        }
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
        final Person internalPerson = personService.getInternalPersonByOrganisationIDOrThrowException(AuthUtils.getOrganisationId());

        final Specification<Booking> bookingSpecification = BookingSpecificationBuilder.fromFilterWithPerson(bookingFilterDto, internalPerson);
        final Page<Booking> ownBookings = bookingRepository.findAll(bookingSpecification, pageable);
        log.debug("Found {} bookings", ownBookings.getTotalElements());
        return ownBookings;
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking createBooking(final Booking booking) {
        if (!securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR)) {
            booking.setInternalNotes(null);
        }
        final Set<Appointment> calculatedAppointments = appointmentService.generateAndLinkAppointments(booking);
        booking.setAppointments(calculatedAppointments);

        UUID personId = booking.getBookedBy().getId();
        Person person = personService.findById(personId);
        if (person instanceof InternalPerson internalPerson) {
            booking.setOrganisationUnit(internalPerson.getOrganisationUnit());
        }

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
        if (!validateBookingAuthority(existingBooking, Roles.TERMIN_ORGANISATOR)) {
            throw new UnauthorizedActionException(MSG_UNAUTHORIZED_ACTION);
        }

        if (!securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR)) {
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

        if (!validateBookingAuthority(existingBooking, Roles.TERMIN_ORGANISATOR)) {
            throw new UnauthorizedActionException(MSG_UNAUTHORIZED_ACTION);
        }
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
        if (savedBooking.getBookedBy() != null) {
            entityManager.detach(savedBooking.getBookedBy());
        }

        return savedBooking;
    }

    /**
     * Validates if the current user has the authority to access or modify a booking.
     *
     * @param booking The booking entity to validate access against.
     * @param role The specific security role that grants overriding access.
     * @return true if the user is authorized; false otherwise.
     */
    public boolean validateBookingAuthority(final Booking booking, final String role) {

        if (securityContextService.hasAuthority(role)) {
            return true;
        }

        final InternalPerson internalPerson = personService.getInternalPersonByOrganisationIDOrThrowException(securityContextService.getCurrentOID());

        return booking.getBookedBy().getId().equals(internalPerson.getId());
    }
}
