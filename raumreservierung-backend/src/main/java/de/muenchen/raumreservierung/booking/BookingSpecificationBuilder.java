package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.booking.dto.BookingFilterDTO;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.room.Room_;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class BookingSpecificationBuilder {

    private BookingSpecificationBuilder() {
    }

    public static <T extends Booking> Specification<T> fromFilter(final BookingFilterDTO bookingFilterDTO) {
        return fromFilterWithPerson(bookingFilterDTO, null);
    }

    public static <T extends Booking> Specification<T> fromFilterWithPerson(final BookingFilterDTO bookingFilterDTO, final Person person) {
        final List<Specification<T>> specificationList = new ArrayList<>();

        if (bookingFilterDTO.roomId() != null) {
            specificationList.add(filterForRoomId(bookingFilterDTO.roomId()));
        }
        if (bookingFilterDTO.start() != null) {
            specificationList.add(filterForStart(bookingFilterDTO.start()));
        }
        if (bookingFilterDTO.end() != null) {
            specificationList.add(filterForEnd(bookingFilterDTO.end()));
        }
        if (person != null && person.getId() != null) {
            specificationList.add(filterForPerson(person));
        }

        return Specification.allOf(specificationList);
    }

    private static <T extends Booking> Specification<T> filterForRoomId(final UUID roomId) {
        return (root, query, cb) -> cb.equal(root.get(Booking_.room).get(Room_.id), roomId);
    }

    private static <T extends Booking> Specification<T> filterForStart(final LocalDateTime start) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(Booking_.schedule).get(ScheduleTemplate_.occupancyStart), start);
    }

    private static <T extends Booking> Specification<T> filterForEnd(final LocalDateTime end) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(Booking_.schedule).get(ScheduleTemplate_.occupancyEnd), end);
    }

    private static <T extends Booking> Specification<T> filterForPerson(final Person person) {
        return (root, query, cb) -> cb.equal(root.get(Booking_.contactPerson), person);
    }
}
