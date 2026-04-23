package de.muenchen.raumreservierung.booking;

import static de.muenchen.raumreservierung.common.ExceptionMessageConstants.MSG_NOT_FOUND;
import de.muenchen.raumreservierung.common.NotFoundException;
import de.muenchen.raumreservierung.security.Authorities;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;

    public Booking getById(final UUID bookingId) {
        return getEntityOrThrowException(bookingId);
    }

    public List<Booking> findAll() {
        final List<Booking> allRooms = bookingRepository.findAll();
        log.debug("Found {} equipments", allRooms.size());
        return allRooms;
    }

    @PreAuthorize(Authorities.BOOKING_MANAGE)
    public Booking createBooking(final Booking room) {
        final Booking newBooking = new Booking();
        newBooking.updateFrom(room);

        final Booking savedRoom = bookingRepository.saveAndFlush(newBooking);
        entityManager.detach(savedRoom);
        if (savedRoom.getContactPerson() != null) {
            entityManager.detach(savedRoom.getContactPerson());
        }

        log.debug("Saved room with id {}", savedRoom.getId());
        return getEntityOrThrowException(savedRoom.getId());
    }

    @PreAuthorize(Authorities.BOOKING_MANAGE)
    public Booking updateRoom(final Booking roomUpdates, final UUID roomId) {
        final Booking existingRoom = getEntityOrThrowException(roomId);
        existingRoom.updateFrom(roomUpdates);

        bookingRepository.saveAndFlush(existingRoom);
        entityManager.detach(existingRoom);
        if (existingRoom.getContactPerson() != null) {
            entityManager.detach(existingRoom.getContactPerson());
        }

        log.debug("Updated room with id {}", existingRoom.getId());
        return getEntityOrThrowException(existingRoom.getId());
    }

    @PreAuthorize(Authorities.BOOKING_MANAGE)
    public void deleteRoom(final UUID roomId) {
        final Booking toDelete = getEntityOrThrowException(roomId);

        log.debug("Deleted room to {}", roomId);
        bookingRepository.deleteById(roomId);
    }

    private Booking getEntityOrThrowException(final UUID bookingId) {
        return bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, bookingId)));
    }
}
