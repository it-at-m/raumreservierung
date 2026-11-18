package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_UNAUTHORIZED_ACTION;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.appointment.AppointmentService;
import de.muenchen.raumreservierung.booking.dto.BookingFilterDTO;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.common.UnauthorizedActionException;
import de.muenchen.raumreservierung.notification.StatusNotificationMailService;
import de.muenchen.raumreservierung.person.PersonService;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.security.AuthUtils;
import de.muenchen.raumreservierung.security.Authorities;
import de.muenchen.raumreservierung.security.Roles;
import de.muenchen.raumreservierung.security.SecurityContextService;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({ "PMD.CommentDefaultAccessModifier" })
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;
    private final SecurityContextService securityContextService;
    private final AppointmentService appointmentService;
    private final PersonService personService;
    private final BookingValidationService bookingValidationService;
    private final StatusNotificationMailService statusNotificationMailService;
    private final BookingUpdateService bookingUpdateService;

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking getById(final UUID bookingId) {
        final Booking booking = getSanitizedBooking(bookingId);
        checkAuthorityOrThrowException(booking, Roles.LESEBERECHTIGT);

        return booking;
    }

    @PreAuthorize(Authorities.BOOKING_READ)
    public Page<Booking> getAllBookingsByPageableAndFilter(final Pageable pageable, final BookingFilterDTO bookingFilterDto) {
        final Specification<Booking> bookingSpecification = BookingSpecificationBuilder.fromFilterWithNew(bookingFilterDto,
                securityContextService.hasAuthority(Roles.RAUM_BUCHUNG));
        return findAllAndFilterSensitiveData(pageable, bookingSpecification);
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Page<Booking> getOwnBookingsByPageableAndFilter(final Pageable pageable, final BookingFilterDTO bookingFilterDto) {
        final Person internalPerson = personService.resolveInternalPersonByOrganisationIDOrThrowException(AuthUtils.getOrganisationId());

        final Specification<Booking> bookingSpecification = BookingSpecificationBuilder.fromFilterWithPerson(bookingFilterDto, internalPerson);
        return findAllAndFilterSensitiveData(pageable, bookingSpecification);
    }

    private Page<Booking> findAllAndFilterSensitiveData(final Pageable pageable, final Specification<Booking> bookingSpecification) {
        final Sort.Order statusOrder = pageable.getSort().getOrderFor("status");

        final Page<Booking> bookings = bookingRepository.findAll(
                statusOrder == null
                        ? bookingSpecification
                        : bookingSpecification.and(BookingSpecifications.withFixedStatusOrder(statusOrder.getDirection())),
                statusOrder == null
                        ? pageable
                        : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));

        if (!securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR)) {
            bookings.forEach(booking -> booking.setInternalNotes(null));
        }

        return bookings;
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking createBooking(final Booking booking) {
        booking.setStatus(BookingStatus.NEW);

        bookingValidationService.bookingIsValidOrThrowException(booking);

        bookingUpdateService.applyOrganizerAuthorityRules(booking, BookingType.DEFAULT, null);

        final Set<Appointment> calculatedAppointments = appointmentService.generateAndLinkAppointments(booking);
        booking.setAppointments(calculatedAppointments);

        assignBookingContext(booking);

        final Booking savedBooking = saveAndDetach(new Booking(), booking);

        log.debug("Created booking with id {}", savedBooking.getId());
        return getSanitizedBooking(savedBooking.getId());
    }

    /**
     * Updates an existing booking. Performs validation on status transitions and handles
     * booking updates accordingly. If the recurrence rule is modified,
     * it regenerates future appointments while preserving the history of past appointments.
     *
     * @param bookingUpdates The booking object containing the updated data.
     * @param bookingId The unique identifier of the booking to be updated.
     * @return The updated, persisted, and sanitized booking entity.
     * @throws NotFoundException if no booking with the given ID exists.
     * @throws UnauthorizedActionException if the user lacks the required booking self-authority.
     * @throws IllegalArgumentException if the booking status transition or terminal state is invalid.
     */
    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking updateBooking(final Booking bookingUpdates, final UUID bookingId) {
        final Booking existingBooking = getEntityOrThrowException(bookingId);
        final BookingStatus oldStatus = existingBooking.getStatus();
        bookingValidationService.validateBookingStatusTransitionOrThrowException(existingBooking, bookingUpdates);
        assignBookingContext(bookingUpdates);
        bookingUpdateService.applyBookingUpdate(existingBooking, bookingUpdates);

        saveAndDetach(existingBooking, bookingUpdates);
        log.debug("Updated booking with id {}", existingBooking.getId());
        statusNotificationMailService.sendStatusNotificationMail(existingBooking, oldStatus);
        return getSanitizedBooking(existingBooking.getId());
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public void deleteBooking(final UUID bookingId) {
        final Booking existingBooking = getEntityOrThrowException(bookingId);
        checkAuthorityOrThrowException(existingBooking, Roles.TERMIN_ORGANISATOR);
        log.debug("Deleted booking with id {}", bookingId);
        bookingRepository.deleteById(bookingId);
    }

    /**
     * Automatically updates and saves a booking's status to {@link BookingStatus#ROOM_CHANGED}
     * after an associated appointment has changed.
     *
     * @param bookingId the id of the booking to process
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAppointmentChange(final UUID bookingId) {
        final Booking bookingToChange = getEntityOrThrowException(bookingId);
        if (bookingValidationService.isObligedToAutomaticStatusChange(bookingToChange)) {
            final Booking bookingChange = new Booking();
            bookingChange.updateFrom(bookingToChange);
            bookingChange.setStatus(BookingStatus.ROOM_CHANGED);

            saveAndDetach(bookingToChange, bookingChange);
        }
    }

    private void checkAuthorityOrThrowException(final Booking booking, final String role) {
        if (!bookingValidationService.validateBookingAuthority(booking, role)) {
            throw new UnauthorizedActionException(MSG_UNAUTHORIZED_ACTION);
        }
    }

    private Booking getSanitizedBooking(final UUID bookingId) {
        final Booking booking = getEntityOrThrowException(bookingId);
        if (!securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR)) {
            booking.setInternalNotes(null);
        }
        return booking;
    }

    Booking getEntityOrThrowException(final UUID bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, bookingId)));
    }

    Booking saveAndDetach(final Booking bookingToUpdate, final Booking sourceData) {
        bookingToUpdate.updateFrom(sourceData);

        final Booking savedBooking = bookingRepository.saveAndFlush(bookingToUpdate);

        entityManager.detach(savedBooking);
        if (savedBooking.getBookedBy() != null) {
            entityManager.detach(savedBooking.getBookedBy());
        }
        if (savedBooking.getBookedFor() != null) {
            entityManager.detach(savedBooking.getBookedFor());
        }
        if (savedBooking.getSeatingType() != null) {
            entityManager.detach(savedBooking.getSeatingType());
        }

        return savedBooking;
    }

    /**
     * Sets the organization unit and determines who the booking is created by and for.
     *
     * @param booking the booking to process and enrich
     * @throws NotFoundException if the current user from the security context cannot be found by oid
     */
    private void assignBookingContext(final Booking booking) {
        final InternalPerson currentPerson = personService.resolveInternalPersonByOrganisationIDOrThrowException(securityContextService.getCurrentOID());
        booking.setOrganisationUnit(currentPerson.getOrganisationUnit());

        if (booking.getBookedFor() == null) {
            booking.setBookedFor(currentPerson);
        }

        final Person bookedFor = (Person) Hibernate.unproxy(booking.getBookedFor());
        if (securityContextService.hasAuthority(Roles.RAUM_ADMIN) && (bookedFor instanceof InternalPerson internalPerson)) {
            booking.setBookedBy(internalPerson);
            booking.setOrganisationUnit(internalPerson.getOrganisationUnit());
        } else {
            booking.setBookedBy(currentPerson);
        }

    }

    void removeSeatingTypeFromBookings(final UUID seatingTypeId) {
        final List<Booking> affectedBookings = bookingRepository.findBySeatingTypeId(seatingTypeId);
        affectedBookings.forEach(booking -> booking.setSeatingType(null));
        bookingRepository.saveAll(affectedBookings);
    }

    boolean existsFutureBookingForRoom(final UUID roomId) {
        final Specification<Booking> spec = BookingSpecificationBuilder.forFutureRoomUsage(roomId);
        return bookingRepository.exists(spec) || appointmentService.existsFutureAppointmentForRoom(roomId);
    }

    void removeRoomFromBookings(final UUID roomId) {
        final List<Booking> affectedBookings = bookingRepository.findByRoomId(roomId);
        affectedBookings.forEach(booking -> booking.setRoom(null));
        bookingRepository.saveAll(affectedBookings);
    }
}
