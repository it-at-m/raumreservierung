package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.booking.events.FutureBookingCheckEvent;
import de.muenchen.raumreservierung.booking.events.RemoveRoomFromBookingsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingEventListener {

    private final BookingService bookingService;

    @EventListener
    public void onFutureBookingCheck(final FutureBookingCheckEvent event) {
        event.setFutureBookingExists(bookingService.existsFutureBookingForRoom(event.getRoomId()));
    }

    @Transactional
    @EventListener
    public void onRemoveRoomFromBookings(final RemoveRoomFromBookingsEvent event) {
        bookingService.removeRoomFromBookings(event.getRoomId());
    }
}
