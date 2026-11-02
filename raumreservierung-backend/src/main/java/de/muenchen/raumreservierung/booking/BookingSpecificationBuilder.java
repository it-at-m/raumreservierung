package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.booking.dto.BookingFilterDTO;
import de.muenchen.raumreservierung.person.domain.Person;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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

        specificationList.add(BookingSpecifications.forRoomId(bookingFilterDTO.roomId()));
        specificationList.add(BookingSpecifications.forStart(normalizeStart(bookingFilterDTO.start())));
        specificationList.add(BookingSpecifications.forEnd(normalizeEnd(bookingFilterDTO.end())));
        specificationList.add(BookingSpecifications.forStatus(bookingFilterDTO.status()));
        specificationList.add(BookingSpecifications.forPerson(person));
        if (!statusNew) {
            specificationList.add(BookingSpecifications.forStatusNotNew());
        }
        specificationList.add(BookingSpecifications.forPersonBookedFor(bookingFilterDTO.bookedForId()));
        specificationList.add(BookingSpecifications.forTitle(bookingFilterDTO.title()));

        return Specification.allOf(specificationList);
    }

    private static OffsetDateTime normalizeStart(final OffsetDateTime start) {
        return start == null ? null : start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime();
    }

    private static OffsetDateTime normalizeEnd(final OffsetDateTime end) {
        return end == null ? null : end.toLocalDate().atTime(LocalTime.MAX).atZone(end.getOffset()).toOffsetDateTime();
    }
}
