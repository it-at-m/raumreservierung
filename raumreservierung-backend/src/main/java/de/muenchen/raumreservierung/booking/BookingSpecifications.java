package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.person.domain.Person_;
import de.muenchen.raumreservierung.room.Room_;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class BookingSpecifications {

    private BookingSpecifications() {
    }

    static <T extends Booking> Specification<T> forRoomId(final UUID roomId) {
        if (roomId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get(Booking_.room).get(Room_.id), roomId);
    }

    static <T extends Booking> Specification<T> forStart(final OffsetDateTime start) {
        if (start == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(Booking_.schedule).get(ScheduleTemplate_.occupancyStart), start);
    }

    static <T extends Booking> Specification<T> forEnd(final OffsetDateTime end) {
        if (end == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(Booking_.schedule).get(ScheduleTemplate_.occupancyEnd), end);
    }

    static <T extends Booking> Specification<T> forStatusNotNew() {
        return (root, query, cb) -> cb.notEqual(root.get(Booking_.status), BookingStatus.NEW);
    }

    static <T extends Booking> Specification<T> forStatus(final List<BookingStatus> status) {
        if (status == null || status.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> root.get(Booking_.status).in(status);
    }

    static <T extends Booking> Specification<T> forPerson(final Person person) {
        if (person == null || person.getId() == null) {
            return null;
        }
        return (root, query, cb) -> cb.or(
                cb.equal(root.get(Booking_.bookedBy), person),
                cb.equal(root.get(Booking_.bookedFor), person));
    }

    static <T extends Booking> Specification<T> forPersonBookedFor(final UUID personId) {
        if (personId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get(Booking_.bookedFor).get(Person_.id), personId);
    }

    static <T extends Booking> Specification<T> forTitle(final String title) {
        if (title == null) {
            return null;
        }
        return (root, query, cb) -> cb.like(cb.lower(root.get(Booking_.title)), "%" + title.toLowerCase(Locale.GERMAN) + "%");
    }

    static <T extends Booking> Specification<T> withFixedStatusOrder(final Sort.Direction direction) {
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
}
