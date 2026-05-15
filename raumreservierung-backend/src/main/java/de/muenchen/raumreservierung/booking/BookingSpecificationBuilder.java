package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.booking.dto.BookingFilterDTO;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.room.Room_;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class BookingSpecificationBuilder {

    private BookingSpecificationBuilder() {
    }

    public static <T extends Booking> Specification<T> fromFilter(final BookingFilterDTO bookingFilterDTO) {
        return fromFilterWithPerson(bookingFilterDTO, null);
    }

    public static <T extends Booking> Specification<T> fromFilterWithPerson(final BookingFilterDTO bookingFilterDTO, final Person person) {
        final List<Specification<T>> specificationList = new ArrayList<>();

        if (bookingFilterDTO.roomName() != null && !bookingFilterDTO.roomName().isBlank()) {
            specificationList.add(filterForRoom(bookingFilterDTO.roomName()));
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

    private static String toLikePattern(final String searchName) {
        return "%" + searchName.toLowerCase(Locale.GERMAN) + "%";
    }

    private static <T extends Booking> Specification<T> filterForRoom(final String searchName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(Booking_.room).get(Room_.name)), toLikePattern(searchName));
    }

    private static <T extends Booking> Specification<T> filterForStart(final LocalDateTime start) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(Booking_.schedule).get(ScheduleTemplate_.occupancyStart), start);
    }

    private static <T extends Booking> Specification<T> filterForEnd(final LocalDateTime end) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(Booking_.schedule).get(ScheduleTemplate_.occupancyEnd), end);
    }

    private static <T extends Booking> Specification<T> filterForPerson(final Person person) {
        return ((root, query, cb) -> cb.equal(root.get(Booking_.contactPerson), person));
    }
}
