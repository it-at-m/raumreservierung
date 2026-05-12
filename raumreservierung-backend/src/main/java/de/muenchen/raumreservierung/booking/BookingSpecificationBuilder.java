package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.booking.dto.BookingFilterDTO;
import de.muenchen.raumreservierung.room.Room_;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public final class BookingSpecificationBuilder {

    private BookingSpecificationBuilder() {
    }

    public static <T extends Booking> Specification<T> fromFilter(final BookingFilterDTO bookingFilterDTO) {
        final List<Specification<T>> specificationList = new ArrayList<>();

        if (bookingFilterDTO.roomName() != null && !bookingFilterDTO.roomName().isBlank()) {
            specificationList.add(filterForRoom(bookingFilterDTO.roomName()));
        }

        return Specification.anyOf(specificationList);
    }

    private static String toLikePattern(final String searchName) {
        return "%" + searchName.toLowerCase(Locale.GERMAN) + "%";
    }

    private static <T extends Booking> Specification<T> filterForRoom(final String searchName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(Booking_.room).get(Room_.name)), toLikePattern(searchName));
    }
}
