package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.booking.events.FutureBookingCheckEvent;
import de.muenchen.raumreservierung.booking.events.RemoveSeatingTypeFromBookingsEvent;
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
        event.setFutureBookingExists(bookingService.existsFutureBookingForSeatingType(event.getSeatingTypeId()));
    }

    @Transactional
    @EventListener
    public void onRemoveRoomFromBookings(final RemoveSeatingTypeFromBookingsEvent event) {
        bookingService.removeSeatingTypeFromBookings(event.getSeatingTypeId());
    }
}
