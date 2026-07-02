package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_ROOM_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_NOT_AVAILABLE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_UNAUTHORIZED_ACTION;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.appointment.AppointmentService;
import de.muenchen.raumreservierung.booking.dto.BookingFilterDTO;
import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.common.UnauthorizedActionException;
import de.muenchen.raumreservierung.person.PersonService;
import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.security.AuthUtils;
import de.muenchen.raumreservierung.security.Authorities;
import de.muenchen.raumreservierung.security.Roles;
import de.muenchen.raumreservierung.security.SecurityContextService;
import jakarta.persistence.EntityManager;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
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
    private final BookingValidationService bookingValidationService;

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking getById(final UUID bookingId) {
        final Booking booking = getSanitizedBooking(bookingId);
        if (!bookingValidationService.validateBookingAuthority(booking, Roles.LESEBERECHTIGT)) {
            throw new UnauthorizedActionException(MSG_UNAUTHORIZED_ACTION);
        }

        return booking;
    }

    @PreAuthorize(Authorities.BOOKING_READ)
    public Page<Booking> getAllBookingsByPageableAndFilter(final Pageable pageable, final BookingFilterDTO bookingFilterDto) {
        final Specification<Booking> bookingSpecification = securityContextService.hasAuthority(Roles.RAUM_BUCHUNG)
                ? BookingSpecificationBuilder.fromFilter(bookingFilterDto)
                : BookingSpecificationBuilder.fromFilterWithNotNew(bookingFilterDto);
        return findAllAndFilterSensitiveData(pageable, bookingSpecification);
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Page<Booking> getOwnBookingsByPageableAndFilter(final Pageable pageable, final BookingFilterDTO bookingFilterDto) {
        final Person internalPerson = personService.resolveInternalPersonByOrganisationIDOrThrowException(AuthUtils.getOrganisationId());

        final Specification<Booking> bookingSpecification = BookingSpecificationBuilder.fromFilterWithPerson(bookingFilterDto, internalPerson);
        return findAllAndFilterSensitiveData(pageable, bookingSpecification);
    }

    private Page<Booking> findAllAndFilterSensitiveData(final Pageable pageable, final Specification<Booking> bookingSpecification) {
        Page<Booking> bookings = bookingRepository.findAll(bookingSpecification, pageable);
        if (!securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR)) {
            bookings = bookings.map(booking -> {
                booking.setInternalNotes(null);
                return booking;
            });
        }

        return bookings;
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking createBooking(final Booking booking) {
        booking.setStatus(BookingStatus.NEW);
        if (!securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR)) {
            booking.setInternalNotes(null);
        }

        if (booking.getRoom() != null && !booking.getRoom().isActive()) {
            throw new BadRequestException(MSG_ROOM_INACTIVE);
        }

        final Set<Appointment> calculatedAppointments = appointmentService.generateAndLinkAppointments(booking);
        booking.setAppointments(calculatedAppointments);

        assignBookingContext(booking);

        if (seatingTypeNotAvailableInRoom(booking)) {
            throw new BadRequestException(MSG_SEATINGTYPE_NOT_AVAILABLE);
        }

        final Booking savedBooking = saveAndDetach(new Booking(), booking);

        log.debug("Created booking with id {}", savedBooking.getId());
        return getSanitizedBooking(savedBooking.getId());
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

        bookingValidationService.validateBookingStatusTransitionOrThrowException(existingBooking, bookingUpdates);

        if (!bookingValidationService.validateBookingAuthority(existingBooking, Roles.TERMIN_ORGANISATOR)) {
            throw new UnauthorizedActionException(MSG_UNAUTHORIZED_ACTION);
        }

        assignBookingContext(bookingUpdates);

        if (!securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR)) {
            bookingUpdates.setInternalNotes(existingBooking.getInternalNotes());
        }

        if (bookingUpdates.getRoom() != null && !bookingUpdates.getRoom().isActive()) {
            throw new BadRequestException(MSG_ROOM_INACTIVE);
        }

        if (seatingTypeNotAvailableInRoom(bookingUpdates)) {
            throw new BadRequestException(MSG_SEATINGTYPE_NOT_AVAILABLE);
        }

        //check if Room, Appointment or Service changed -> automatically change to Status ROOM_CHANGED, except if role Terminorganisator or higher
        if (bookingValidationService.isObligedToAutomaticStatusChange(existingBooking) && needForAutomaticStatusChange(existingBooking, bookingUpdates)) {
            bookingUpdates.setStatus(BookingStatus.ROOM_CHANGED);
        }

        updateBookingAppointments(existingBooking, bookingUpdates);

        saveAndDetach(existingBooking, bookingUpdates);

        log.debug("Updated booking with id {}", existingBooking.getId());
        return getSanitizedBooking(existingBooking.getId());

    }

    /**
     * Determines whether an automatic status change is required based on modifications
     * to the booking details.
     *
     * @param existingBooking the current booking before updates
     * @param bookingUpdate the updated booking information
     * @return true if any of the following have changed: the room, the appointments,
     *         or the service requirement; false otherwise
     */
    private boolean needForAutomaticStatusChange(final Booking existingBooking, final Booking bookingUpdate) {
        return !Objects.equals(existingBooking.getRoom(), bookingUpdate.getRoom()) // room changed
                || !Objects.equals(existingBooking.getEquipment(), bookingUpdate.getEquipment()) // service changed
                || !Objects.equals(existingBooking.getSeatingType(), bookingUpdate.getSeatingType())
                || existingBooking.getParticipantCount() != bookingUpdate.getParticipantCount()
                || existingBooking.isCateringNeeded() != bookingUpdate.isCateringNeeded()
                || !Objects.equals(existingBooking.getRecurringRule(), bookingUpdate.getRecurringRule()) // schedule changed
                || !Objects.equals(existingBooking.getSchedule(), bookingUpdate.getSchedule());
    }

    /**
     * Updates the appointments of a booking when its recurring rule changes.
     * It preserves all past appointments and merges them with newly generated future appointments based
     * on the updated rule.
     *
     * @param existingBooking the current state of the booking
     * @param bookingUpdates the updated booking data
     */
    public void updateBookingAppointments(final Booking existingBooking, final Booking bookingUpdates) {
        if (Objects.equals(existingBooking.getRecurringRule(), bookingUpdates.getRecurringRule())) {
            return;
        }

        final Set<Appointment> newAppointments = appointmentService.generateAndLinkAppointments(bookingUpdates);
        final OffsetDateTime now = OffsetDateTime.now();

        final Set<Appointment> pastAppointments = existingBooking.getAppointments().stream()
                .filter(a -> a.getSchedule().occupancyStart().isBefore(now))
                .collect(Collectors.toSet());

        final Set<Appointment> futureNewAppointments = newAppointments.stream()
                .filter(a -> a.getSchedule().occupancyStart().isAfter(now))
                .collect(Collectors.toSet());

        pastAppointments.addAll(futureNewAppointments);

        bookingUpdates.setAppointments(pastAppointments);
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public void deleteBooking(final UUID bookingId) {
        final Booking existingBooking = getEntityOrThrowException(bookingId);

        if (!bookingValidationService.validateBookingAuthority(existingBooking, Roles.TERMIN_ORGANISATOR)) {
            throw new UnauthorizedActionException(MSG_UNAUTHORIZED_ACTION);
        }
        log.debug("Deleted booking with id {}", bookingId);
        bookingRepository.deleteById(bookingId);
    }

    private Booking getSanitizedBooking(final UUID bookingId) {
        final Booking booking = getEntityOrThrowException(bookingId);
        if (!securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR)) {
            booking.setInternalNotes(null);
        }
        return booking;
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
        if (savedBooking.getBookedFor() != null) {
            entityManager.detach(savedBooking.getBookedFor());
        }
        if (savedBooking.getSeatingType() != null) {
            entityManager.detach(savedBooking.getSeatingType());
        }

        return savedBooking;
    }

    /**
     * Validates if the selected seating type is available within the booked room's capacities.
     *
     * @param booking Booking containing the room and requested seating type.
     * @return true if the seating type is not available in selected room or no room is selected; false
     *         otherwise.
     */
    public boolean seatingTypeNotAvailableInRoom(final Booking booking) {
        return booking.getSeatingType() != null && Optional.ofNullable(booking.getRoom())
                .map(Room::getRoomSeatingCapacities)
                .map(capacities -> capacities.stream()
                        .noneMatch(capacity -> Objects.equals(capacity.getSeatingType(), booking.getSeatingType())))
                .orElse(true);
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
        if (securityContextService.hasAuthority(Roles.RAUM_ADMIN) && !(bookedFor instanceof ExternalPerson)) {
            booking.setBookedBy(bookedFor);
        } else {
            booking.setBookedBy(currentPerson);
        }

    }

}
