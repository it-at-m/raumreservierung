package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.person.PersonDeletionListener;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingPersonDeletionListener implements PersonDeletionListener {

    private final BookingRepository bookingRepository;

    /**
     * Changes the field {@link Booking#getBookedFor()} of all bookings,
     * where a person is assigned as {@link Booking#getBookedFor()}, to the person that is assigned
     * under {@link Booking#getBookedBy()}.
     *
     * @param personId the id of the deleted person.
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void onPersonDeleted(final UUID personId) {
        final List<Booking> affectedBookings = bookingRepository.findAllByBookedForId(personId);
        affectedBookings.forEach(booking -> booking.setBookedFor(booking.getBookedBy()));
        bookingRepository.saveAll(affectedBookings);
    }
}
