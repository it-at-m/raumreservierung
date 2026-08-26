package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.raumreservierung.common.NotFoundException;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public class BookingPersistenceHelper {

    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;

    Booking getEntityOrThrowException(final UUID bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, bookingId)));
    }

    Booking saveAndDetach(final Booking bookingToUpdate, final Booking sourceData) {
        bookingToUpdate.updateFrom(sourceData);

        final Booking savedBooking = bookingRepository.saveAndFlush(bookingToUpdate);

        entityManager.detach(savedBooking);
        if (savedBooking.getBookedBy() != null) {
            entityManager.detach(savedBooking.getBookedBy());
        }
        if (savedBooking.getBookedFor() != null) {
            entityManager.detach(savedBooking.getBookedFor());
        }
        if (savedBooking.getSeatingType() != null) {
            entityManager.detach(savedBooking.getSeatingType());
        }

        return savedBooking;
    }
}
