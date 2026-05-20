package de.muenchen.raumreservierung.validation;

import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class BookingParticipantValidator implements ConstraintValidator<ValidParticipantCount, Booking> {

    @Autowired
    private RoomService roomService;

    @Override
    public boolean isValid(Booking booking, ConstraintValidatorContext context) {
        int count = booking.getParticipantCount();

        if (count <= 0 || count >= 1000) {
            return false;
        }

        if (booking.getRoom() != null) {
            Room room = booking.getRoom();
            return count <= room.getCapacity();
        }

        int absoluteMax = roomService.findAbsoluteMaxCapacity();
        return count <= absoluteMax;
    }
}
