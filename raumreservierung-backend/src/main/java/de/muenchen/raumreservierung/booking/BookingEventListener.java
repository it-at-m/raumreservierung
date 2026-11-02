package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.person.PersonDeleteEvent;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class BookingEventListener {

    private final BookingRepository bookingRepository;
    private final BookingValidationService bookingValidationService;
    private final BookingService bookingService;

    /**
     * Automatically updates and saves a booking's status to {@link BookingStatus#ROOM_CHANGED}
     * after an associated appointment has changed.
     *
     * @param bookingId the id of the booking to process
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAppointmentChange(final UUID bookingId) {
        final Booking bookingToChange = bookingService.getEntityOrThrowException(bookingId);
        if (bookingValidationService.isObligedToAutomaticStatusChange(bookingToChange)) {
            final Booking bookingChange = new Booking();
            bookingChange.updateFrom(bookingToChange);
            bookingChange.setStatus(BookingStatus.ROOM_CHANGED);

            bookingService.saveAndDetach(bookingToChange, bookingChange);
        }
    }

    /**
     * Changes the field {@link Booking#getBookedFor()} of all bookings,
     * where a person is assigned as {@link Booking#getBookedFor()}, to the person that is assigned
     * under {@link Booking#getBookedBy()}.
     *
     * @param event contains the person id.
     */
    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    public void onPersonDeleted(final PersonDeleteEvent event) {
        final List<Booking> affectedBookings = bookingRepository.findAllByBookedForId(event.personId());
        affectedBookings.forEach(booking -> booking.setBookedFor(booking.getBookedBy()));
        bookingRepository.saveAll(affectedBookings);
    }
}
