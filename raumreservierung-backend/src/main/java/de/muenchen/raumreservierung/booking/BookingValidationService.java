package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_EQUIPMENT_INACTIVE;
import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_SEATINGTYPE_INACTIVE;

import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.seating.SeatingType;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingValidationService {

    public void bookingIsValidOrThrowException(final Booking bookingUpdates) {
        bookingIsValidOrThrowException(bookingUpdates, null);
    }

    public void bookingIsValidOrThrowException(final Booking bookingUpdates, final Booking existingBooking) {
        if (equipmentInactive(bookingUpdates, existingBooking)) {
            throw new BadRequestException(MSG_EQUIPMENT_INACTIVE);
        }
        if (seatingTypeInactive(bookingUpdates, existingBooking)) {
            throw new BadRequestException(MSG_SEATINGTYPE_INACTIVE);
        }
    }

    private boolean equipmentInactive(final Booking bookingUpdate, final Booking existingBooking) {
        boolean hasNewInactives = false;
        final Set<Equipment> equipmentsUpdate = bookingUpdate.getEquipment();
        if (equipmentsUpdate != null) {
            final Set<Equipment> equipmentsNewlyAdded = new HashSet<>(equipmentsUpdate);
            if (existingBooking != null && existingBooking.getEquipment() != null) {
                equipmentsNewlyAdded.removeAll(existingBooking.getEquipment());
            }
            hasNewInactives = equipmentsNewlyAdded.stream().anyMatch(e -> !e.isActive());
        }
        return hasNewInactives;
    }

    private boolean seatingTypeInactive(final Booking bookingUpdate, final Booking existingBooking) {
        final SeatingType seatingTypeUpdate = bookingUpdate.getSeatingType();
        final boolean isDiffering = existingBooking == null || existingBooking.getSeatingType() == null
                || !Objects.equals(seatingTypeUpdate, existingBooking.getSeatingType());
        return isDiffering && seatingTypeUpdate != null && !seatingTypeUpdate.isActive();
    }

}
