package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.booking.dto.BookingFilterDTO;
import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.room.Room_;
import de.muenchen.raumreservierung.seating.SeatingType_;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public final class BookingSpecificationBuilder {

    private BookingSpecificationBuilder() {
    }

    public static <T extends Booking> Specification<T> fromFilterWithNew(final BookingFilterDTO bookingFilterDTO, final boolean withNew) {
        return fromFilterWithPersonOrStatusNew(bookingFilterDTO, null, withNew);
    }

    public static <T extends Booking> Specification<T> fromFilterWithPerson(final BookingFilterDTO bookingFilterDTO, final Person person) {
        return fromFilterWithPersonOrStatusNew(bookingFilterDTO, person, true);
    }

    public static <T extends Booking> Specification<T> fromFilterWithPersonOrStatusNew(final BookingFilterDTO bookingFilterDTO, final Person person,
            final boolean statusNew) {
        final List<Specification<T>> specificationList = new ArrayList<>();

        if (bookingFilterDTO.roomId() != null) {
            specificationList.add(filterForRoomId(bookingFilterDTO.roomId()));
        }
        final OffsetDateTime start = bookingFilterDTO.start();
        if (start != null) {
            specificationList.add(filterForStart(start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime()));
        }
        final OffsetDateTime end = bookingFilterDTO.end();
        if (end != null) {
            specificationList.add(filterForEnd(end.toLocalDate().atTime(LocalTime.MAX).atZone(end.getOffset()).toOffsetDateTime()));
        }
        final List<BookingStatus> statusList = bookingFilterDTO.status();
        if (statusList != null && !statusList.isEmpty()) {
            specificationList.add(filterForStatus(statusList));
        }
        if (person != null && person.getId() != null) {
            specificationList.add(filterForPerson(person));
        }
        if (!statusNew) {
            specificationList.add(filterForStatusNotNew());
        }

        return Specification.allOf(specificationList);
    }

    private static <T extends Booking> Specification<T> filterForRoomId(final UUID roomId) {
        return (root, query, cb) -> cb.equal(root.get(Booking_.room).get(Room_.id), roomId);
    }

    private static <T extends Booking> Specification<T> filterForStart(final OffsetDateTime start) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(Booking_.schedule).get(ScheduleTemplate_.occupancyStart), start);
    }

    private static <T extends Booking> Specification<T> filterForEnd(final OffsetDateTime end) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(Booking_.schedule).get(ScheduleTemplate_.occupancyEnd), end);
    }

    private static <T extends Booking> Specification<T> filterForStatusNotNew() {
        return (root, query, cb) -> cb.notEqual(root.get(Booking_.status), BookingStatus.NEW);
    }

    private static <T extends Booking> Specification<T> filterForStatus(final List<BookingStatus> status) {
        return (root, query, cb) -> root.get(Booking_.status).in(status);
    }

    private static <T extends Booking> Specification<T> filterForPerson(final Person person) {
        return (root, query, cb) -> cb.or(
                cb.equal(root.get(Booking_.bookedBy), person),
                cb.equal(root.get(Booking_.bookedFor), person));
    }

    public static <T extends Booking> Specification<T> withFixedStatusOrder(final Sort.Direction direction) {
        return (root, query, cb) -> {
            CriteriaBuilder.Case<Integer> order = cb.selectCase();

            for (final BookingStatus value : BookingStatus.values()) {
                order = order.when(cb.equal(root.get(Booking_.status), value), value.getSortOrder());
            }

            if (query != null) {
                query.orderBy(direction.isAscending() ? cb.asc(order) : cb.desc(order));
            }
            return null;
        };
    }

    private static <T extends Booking> Specification<T> filterForSeatingTypeId(final UUID seatingTypeId) {
        return (root, query, cb) -> cb.equal(root.get(Booking_.seatingType).get(SeatingType_.id), seatingTypeId);
    }

    private static <T extends Booking> Specification<T> filterForOccupancyEndAfter(final OffsetDateTime now) {
        return (root, query, cb) -> cb.greaterThan(root.get(Booking_.schedule).get(ScheduleTemplate_.occupancyEnd), now);
    }

    private static <T extends Booking> Specification<T> filterExcludingStatus(final BookingStatus... status) {
        return (root, query, cb) -> cb.not(root.get(Booking_.status).in(status));
    }

    public static <T extends Booking> Specification<T> forFutureSeatingTypeUsage(final UUID seatingTypeId) {
        return Specification.allOf(
                filterForSeatingTypeId(seatingTypeId),
                filterForOccupancyEndAfter(OffsetDateTime.now()),
                filterExcludingStatus(BookingStatus.CANCELED, BookingStatus.UNFEASIBLE, BookingStatus.NEW));
    }
}
