package de.muenchen.raumreservierung.booking.types;

import de.muenchen.raumreservierung.security.Authorities;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class BookingStatusService {

    @PreAuthorize(Authorities.BOOKING_STATE_UPDATE)
    public BookingStatus updateBookingState(final BookingStatus currentStatus, final BookingState nextState) {
        final BookingState currentState = currentStatus.bookingState();
        BookingSubStatus nextSub = currentStatus.bookingSubStatus();

        if (nextState == BookingState.BLOCKED) {
            nextSub = BookingSubStatus.PREVIEW;
        }
        if (currentState == BookingState.BLOCKED && nextState == BookingState.BOOKED) {
            nextSub = BookingSubStatus.NEW;
        }

        return new BookingStatus(nextState, nextSub);
    }

    @PreAuthorize(Authorities.BOOKING_SUBSTATE_UPDATE)
    public BookingStatus updateBookingSubStatus(final BookingStatus status, final BookingSubStatus nextSub) {
        BookingStatus updatedStatus = status;
        if (nextSub == BookingSubStatus.IN_PROGRESS || nextSub == BookingSubStatus.AGREED) {
            updatedStatus = new BookingStatus(status.bookingState(), nextSub);
        }
        return updatedStatus;
    }
}
