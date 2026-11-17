package de.muenchen.raumreservierung.appointment;

import de.muenchen.raumreservierung.booking.BookingStatus;
import de.muenchen.raumreservierung.booking.Booking_;
import de.muenchen.raumreservierung.booking.ScheduleTemplate_;
import de.muenchen.raumreservierung.room.Room_;
import de.muenchen.raumreservierung.seating.SeatingType_;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class AppointmentSpecifications {
    private AppointmentSpecifications(){
    }
    static <T extends Appointment> Specification<T> filterForRoomId(final UUID roomId) {
        if (roomId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get(Appointment_.booking).get(Booking_.room).get(Room_.id), roomId);
    }

    static <T extends Appointment> Specification<T> filterForSeatingTypeId(final UUID seatingTypeId) {
        return (root, query, cb) -> cb.equal(root.get(Appointment_.booking).get(Booking_.seatingType).get(SeatingType_.id), seatingTypeId);
    }

    static <T extends Appointment> Specification<T> filterExcludingStatus(final BookingStatus... status) {
        return (root, query, cb) -> cb.not(root.get(Appointment_.booking).get(Booking_.status).in(List.of(status)));
    }

    static <T extends Appointment> Specification<T> filterForStatus(final BookingStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> root.get(Appointment_.booking).get(Booking_.status).in(status);
    }

    static <T extends Appointment> Specification<T> filterForRoomIds(final List<UUID> roomIds) {
        if (roomIds == null || roomIds.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> root.get(Appointment_.booking).get(Booking_.room).get(Room_.id).in(roomIds);
    }

    static <T extends Appointment> Specification<T> filterForBookingId(final UUID bookingId) {
        if (bookingId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get(Appointment_.booking).get(Booking_.id), bookingId);
    }

    static <T extends Appointment> Specification<T> filterForStartDate(final OffsetDateTime start) {
        if (start == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(Appointment_.schedule).get(ScheduleTemplate_.occupancyStart), start);
    }

    static <T extends Appointment> Specification<T> filterForEndDate(final OffsetDateTime end) {
        if (end == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(Appointment_.schedule).get(ScheduleTemplate_.occupancyEnd), end);
    }

    static <T extends Appointment> Specification<T> filterForOccupancyEndAfter(final OffsetDateTime now) {
        return (root, query, cb) -> cb.greaterThan(root.get(Appointment_.schedule).get(ScheduleTemplate_.occupancyEnd), now);
    }

}
