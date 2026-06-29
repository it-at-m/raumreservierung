package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_EQUIPMENT_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_INACTIVE;

import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.seating.SeatingType;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingValidationService {

    /**
     * Validates a new booking.
     *
     * @param booking the booking data to validate
     * @throws BadRequestException if any selected resource is inactive
     */
    public void bookingIsValidOrThrowException(final Booking booking) {
        bookingIsValidOrThrowException(booking, null);
    }

    /**
     * Validates a booking update against an existing booking.
     *
     * @param bookingUpdates the new booking data
     * @param existingBooking the original booking, or null if new
     * @throws BadRequestException if newly added equipment or seating type is inactive
     */
    public void bookingIsValidOrThrowException(final Booking bookingUpdates, final Booking existingBooking) {
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

}
