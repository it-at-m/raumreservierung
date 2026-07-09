package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_EQUIPMENT_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_PARTICIPANT_COUNT_INVALID;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_ROOM_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_NOT_AVAILABLE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_STATUS_CHANGE_NOT_POSSIBLE;

import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.person.PersonService;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomSeatingCapacity;
import de.muenchen.raumreservierung.room.RoomService;
import de.muenchen.raumreservierung.seating.SeatingType;
import de.muenchen.raumreservierung.security.Roles;
import de.muenchen.raumreservierung.security.SecurityContextService;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingValidationService {
    private final RoomService roomService;
    private final BookingTransitionService bookingTransitionService;
    private final SecurityContextService securityContextService;
    private final PersonService personService;

    /**
     *
     * Validates if the selected seating type is available within the booked room's capacities.
     *
     * @param booking Booking containing the room and requested seating type.
     * @return true if the seating type is not available in selected room or no room is selected; false
     *         otherwise.
     */
    private boolean seatingTypeNotAvailableInRoom(final Booking booking) {
        return booking.getSeatingType() != null && Optional.ofNullable(booking.getRoom())
                .map(Room::getRoomSeatingCapacities)
                .map(capacities -> capacities.stream()
                        .noneMatch(capacity -> Objects.equals(capacity.getSeatingType(), booking.getSeatingType())))
                .orElse(true);
    }

    /**
     * Checks if the booking's participant count exceeds the allowed capacity or is negative.
     * A count of 0 is treated as unset and considered valid.
     *
     * @param booking the booking to validate
     * @return true if the count is invalid, false otherwise
     */
    private boolean participantCountNotValid(final Booking booking) {
        final int count = booking.getParticipantCount();
        return count != 0 && (count < 0 || count > determineMaxCapacity(booking));
    }

    /**
     * Determines the maximum capacity for a booking.
     * Falls back to global maximum if no room is set, or to the room's base capacity
     * if no matching seating arrangement is found.
     *
     * @param booking the booking containing room and seating type
     * @return the maximum capacity, or {@code -1} if the requested seating type has no configured
     *         capacity
     */
    private int determineMaxCapacity(final Booking booking) {
        final Room room = booking.getRoom();

        if (room == null) {
            return roomService.findAbsoluteMaxCapacity();
        }

        if (booking.getSeatingType() != null && room.getRoomSeatingCapacities() != null) {
            return room.getRoomSeatingCapacities().stream()
                    .filter(rsc -> rsc.getSeatingType().equals(booking.getSeatingType()))
                    .mapToInt(RoomSeatingCapacity::getCapacity)
                    .max()
                    .orElse(-1);
        }

        return room.getCapacity();
    }

    /**
     * Checks if the room was changed from active to an inactive room.
     *
     * @param bookingUpdates The updated booking data.
     * @param existingBooking The original booking data, can be null.
     * @return true if the room is changed from active to inactive; false otherwise.
     */
    private boolean roomChangedToInactive(final Booking bookingUpdates, final Booking existingBooking) {
        final boolean noExistingRoomOrNotSameRoom = existingBooking == null || !Objects.equals(bookingUpdates.getRoom(), existingBooking.getRoom());
        return noExistingRoomOrNotSameRoom && bookingUpdates.getRoom() != null && !bookingUpdates.getRoom().isActive();
    }

    /**
     * Checks if a booking can be canceled by the current user.
     * Only permitted if the user has role RAUM_BUCHUNG or higher
     * and the transition to the CANCELED status is allowed.
     *
     * @param booking the booking entity to check
     * @return true if the user is authorized and the status transition is valid, false otherwise
     */
    public boolean canCancelBooking(final Booking booking) {
        return validateBookingAuthority(booking, Roles.RAUM_BUCHUNG) &&
                bookingTransitionService.isTransitionAllowed(booking.getStatus(), BookingStatus.CANCELED);
    }

    /**
     * Determines whether the booking status must change automatically.
     * Automatic changes only in bookings with state COORDINATION_NEEDED, ROOM_APPROVED or
     * ORGANIZER_APPROVED.
     * Users with role TERMIN_ORGANISATOR or higher are explicitly excepted from this automation.
     *
     * @param booking the booking entity to evaluate
     * @return true if the booking is in an eligible state and the user is not a coordinator or higher,
     *         false otherwise
     */
    public boolean isObligedToAutomaticStatusChange(final Booking booking) {
        return (booking.getStatus() == BookingStatus.COORDINATION_NEEDED
                || booking.getStatus() == BookingStatus.ROOM_APPROVED
                || booking.getStatus() == BookingStatus.ORGANIZER_APPROVED)
                && !securityContextService.hasAuthority(Roles.TERMIN_ORGANISATOR);
    }

    /**
     * Validates if the current user has the authority to access or modify a booking.
     *
     * @param booking The booking entity to validate access against.
     * @param role The specific security role that grants overriding access.
     * @return true if the user is authorized; false otherwise.
     */
    public boolean validateBookingAuthority(final Booking booking, final String role) {
        return securityContextService.hasAuthority(role) || isOwner(booking);
    }

    /**
     * Validates the status transition for a booking update.
     *
     * @param existingBooking the current booking state
     * @param bookingUpdates the requested booking updates
     * @throws BadRequestException if the status transition or cancellation is invalid
     */
    public void validateBookingStatusTransitionOrThrowException(final Booking existingBooking, final Booking bookingUpdates) {
        if (existingBooking != null) {
            final boolean isTransitionAllowed = bookingTransitionService.isTransitionAllowed(existingBooking.getStatus(), bookingUpdates.getStatus());
            final boolean isIllegalCancel = bookingUpdates.getStatus() == BookingStatus.CANCELED && !canCancelBooking(existingBooking);

            if (!isTransitionAllowed || isIllegalCancel) {
                throw new BadRequestException(MSG_STATUS_CHANGE_NOT_POSSIBLE);
            }
        }
    }

    /**
     * Validates a new booking.
     *
     * @param booking the booking data to validate
     * @throws BadRequestException if the room is inactive, seating type is missing, capacity is
     *             exceeded, or if any selected resource is inactive
     */
    public void bookingIsValidOrThrowException(final Booking booking) {
        bookingIsValidOrThrowException(booking, null);
    }

    /**
     * Validates the booking updates against room availability, seating types, capacities and changed
     * resources,
     * taking into account an existing booking.
     *
     * @param bookingUpdates the booking data containing the requested updates
     * @param existingBooking the original booking before updates, or null if new
     * @throws BadRequestException if the room is inactive, seating type is missing, capacity is
     *             exceeded, newly added equipment or seating type is inactive
     */
    public void bookingIsValidOrThrowException(final Booking bookingUpdates, final Booking existingBooking) {
        resourcesActiveOrThrowException(bookingUpdates, existingBooking);
        validateBookingStatusTransitionOrThrowException(existingBooking, bookingUpdates);
        if (seatingTypeNotAvailableInRoom(bookingUpdates)) {
            throw new BadRequestException(MSG_SEATINGTYPE_NOT_AVAILABLE);
        }
        if (participantCountNotValid(bookingUpdates)) {
            throw new BadRequestException(MSG_PARTICIPANT_COUNT_INVALID);
        }
    }

    public void resourcesActiveOrThrowException(final Booking bookingUpdates, final Booking existingBooking) {
        if (roomChangedToInactive(bookingUpdates, existingBooking)) {
            throw new BadRequestException(MSG_ROOM_INACTIVE);
        }
        if (equipmentInactive(bookingUpdates, existingBooking)) {
            throw new BadRequestException(MSG_EQUIPMENT_INACTIVE);
        }
        if (seatingTypeInactive(bookingUpdates, existingBooking)) {
            throw new BadRequestException(MSG_SEATINGTYPE_INACTIVE);
        }
    }

    /**
     * Checks if the update adds any new inactive equipment.
     * (Updating existing inactive equipment from the original booking is allowed.)
     *
     * @param bookingUpdate the new booking data
     * @param existingBooking the original booking, or null
     * @return true if newly added equipment is inactive
     */
    private boolean equipmentInactive(final Booking bookingUpdate, final Booking existingBooking) {
        final Set<Equipment> existingEquipment = Optional.ofNullable(existingBooking)
                .map(Booking::getEquipment)
                .orElse(Collections.emptySet());

        return Objects.requireNonNullElse(bookingUpdate.getEquipment(), Collections.<Equipment>emptySet()).stream()
                .filter(eq -> !existingEquipment.contains(eq))
                .anyMatch(eq -> !eq.isActive());
    }

    /**
     * Checks if the seating type is changed to a different, inactive one.
     * (Keeping the existing inactive seating type from the original booking is allowed.)
     *
     * @param bookingUpdate the new booking data
     * @param existingBooking the original booking, or null
     * @return true if the seating type is modified and the new one is inactive
     */
    private boolean seatingTypeInactive(final Booking bookingUpdate, final Booking existingBooking) {
        final Optional<SeatingType> existingSeatingType = Optional.ofNullable(existingBooking).map(Booking::getSeatingType);

        final Optional<SeatingType> seatingTypeUpdate = Optional.ofNullable(bookingUpdate.getSeatingType());

        return !Objects.equals(seatingTypeUpdate, existingSeatingType)
                && seatingTypeUpdate.filter(s -> !s.isActive()).isPresent();
    }

    private boolean isOwner(final Booking booking) {
        final InternalPerson internalPerson = personService.getInternalPersonByOrganisationIDOrThrowException(securityContextService.getCurrentOID());

        return booking.getBookedBy().getId().equals(internalPerson.getId()) || booking.getBookedFor().getId().equals(internalPerson.getId());
    }

}
