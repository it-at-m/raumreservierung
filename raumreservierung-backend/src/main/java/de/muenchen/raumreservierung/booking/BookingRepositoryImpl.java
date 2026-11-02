package de.muenchen.raumreservierung.booking;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public void detach(final Booking booking) {
        entityManager.detach(booking);
        if (booking.getBookedBy() != null) {
            entityManager.detach(booking.getBookedBy());
        }
        if (booking.getBookedFor() != null) {
            entityManager.detach(booking.getBookedFor());
        }
        if (booking.getSeatingType() != null) {
            entityManager.detach(booking.getSeatingType());
        }
    }
}
