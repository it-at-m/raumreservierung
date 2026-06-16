package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_PARTICIPANT_COUNT_INVALID;
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
import de.muenchen.raumreservierung.room.RoomSeatingCapacity;
import de.muenchen.raumreservierung.room.RoomService;
import de.muenchen.raumreservierung.seating.SeatingType;
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
    private final RoomService roomService;

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
        final Person internalPerson = personService.resolveInternalPersonByOrganisationIDOrThrowException(AuthUtils.getOrganisationId());

        final Specification<Booking> bookingSpecification = BookingSpecificationBuilder.fromFilterWithPerson(bookingFilterDto, internalPerson);
        final Page<Booking> ownBookings = bookingRepository.findAll(bookingSpecification, pageable);
        log.debug("Found {} bookings", ownBookings.getTotalElements());
        return ownBookings;
    }

    @PreAuthorize(Authorities.BOOKING_SELF)
    public Booking createBooking(final Booking booking) {
        bookingIsValidOrThrowException(booking);

        if (!securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR)) {
            booking.setInternalNotes(null);
        }

        final Set<Appointment> calculatedAppointments = appointmentService.generateAndLinkAppointments(booking);
        booking.setAppointments(calculatedAppointments);

        assignBookingContext(booking);

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
        bookingIsValidOrThrowException(bookingUpdates);

        final Booking existingBooking = getEntityOrThrowException(bookingId);
        if (!validateBookingAuthority(existingBooking, Roles.TERMIN_ORGANISATOR)) {
            throw new UnauthorizedActionException(MSG_UNAUTHORIZED_ACTION);
        }

        assignBookingContext(bookingUpdates);

        if (!securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR)) {
            bookingUpdates.setInternalNotes(existingBooking.getInternalNotes());
        }

        updateBookingAppointments(existingBooking, bookingUpdates);

        saveAndDetach(existingBooking, bookingUpdates);

        log.debug("Updated booking with id {}", existingBooking.getId());
        return getEntityOrThrowException(existingBooking.getId());

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
        if (savedBooking.getBookedFor() != null) {
            entityManager.detach(savedBooking.getBookedFor());
        }
        if (savedBooking.getSeatingType() != null) {
            entityManager.detach(savedBooking.getSeatingType());
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

        final InternalPerson internalPerson = personService.resolveInternalPersonByOrganisationIDOrThrowException(securityContextService.getCurrentOID());

        return booking.getBookedBy().getId().equals(internalPerson.getId());
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

    /**
     * Checks if the booking's participant count exceeds the allowed capacity.
     * A count of 0 is treated as unset and considered valid.
     *
     * @param booking the booking to validate
     * @return true if the count is invalid, false otherwise
     */
    private boolean participantCountNotValid(final Booking booking) {
        final int count = booking.getParticipantCount();

        if (count == 0) {
            return false;
        }

        if (booking.getRoom() != null) {
            final Room room = booking.getRoom();
            if (booking.getSeatingType() != null && room.getRoomSeatingCapacities() != null) {
                final SeatingType seatingType = booking.getSeatingType();
                final Set<RoomSeatingCapacity> roomSeatingCapacities = room.getRoomSeatingCapacities();
                return roomSeatingCapacities.stream().filter(rsc -> rsc.getSeatingType().equals(seatingType)).allMatch(rsc -> rsc.getCapacity() < count);
            }
            return count > room.getCapacity();
        } else {
            final int absoluteMax = roomService.findAbsoluteMaxCapacity();
            return count > absoluteMax;
        }
    }

    /**
     * Validates the booking updates against room availability, seating types, and capacities.
     *
     * @param bookingUpdates the booking data containing the requested updates
     * @throws BadRequestException if the room is inactive, seating type is missing, or capacity is
     *             exceeded
     */
    private void bookingIsValidOrThrowException(final Booking bookingUpdates) {
        if (bookingUpdates.getRoom() != null && !bookingUpdates.getRoom().isActive()) {
            throw new BadRequestException(MSG_ROOM_INACTIVE);
        }
        if (seatingTypeNotAvailableInRoom(bookingUpdates)) {
            throw new BadRequestException(MSG_SEATINGTYPE_NOT_AVAILABLE);
        }
        if (participantCountNotValid(bookingUpdates)) {
            throw new BadRequestException(MSG_PARTICIPANT_COUNT_INVALID);
        }
    }

}
