package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_EQUIPMENT_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_PARTICIPANT_COUNT_INVALID;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_ROOM_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_NOT_AVAILABLE;

import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.seating.SeatingType;
import java.util.Collections;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomSeatingCapacity;
import de.muenchen.raumreservierung.room.RoomService;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingValidationService {
    private final RoomService roomService;

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
     * VValidates a new booking.
     *
     * @param booking the booking data to validate
     * @throws BadRequestException if the room is inactive, seating type is missing, capacity is
     *             exceeded, or if any selected resource is inactive
     */
    public void bookingIsValidOrThrowException(final Booking booking) {
        bookingIsValidOrThrowException(booking, null);
    }

    /**
     * Validates the booking updates against room availability, seating types, capacities and changed resources,
     * taking into account an existing booking.
     *
     * @param bookingUpdates the booking data containing the requested updates
     * @param existingBooking the original booking before updates, or null if new
     * @throws BadRequestException if the room is inactive, seating type is missing, capacity is
     *             exceeded, newly added equipment or seating type is inactive
     */
    public void bookingIsValidOrThrowException(final Booking bookingUpdates, final Booking existingBooking) {
        if (roomChangedToInactive(bookingUpdates, existingBooking)) {
            throw new BadRequestException(MSG_ROOM_INACTIVE);
            }
        if (equipmentInactive(bookingUpdates, existingBooking)) {
            throw new BadRequestException(MSG_EQUIPMENT_INACTIVE);
        }
        if (seatingTypeNotAvailableInRoom(bookingUpdates)) {
            throw new BadRequestException(MSG_SEATINGTYPE_NOT_AVAILABLE);
            }
        if (seatingTypeInactive(bookingUpdates, existingBooking)) {
            throw new BadRequestException(MSG_SEATINGTYPE_INACTIVE);
        }
        if (participantCountNotValid(bookingUpdates)) {
            throw new BadRequestException(MSG_PARTICIPANT_COUNT_INVALID);
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

}
