package de.muenchen.raumreservierung.appointment;

import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.booking.Booking_;
import de.muenchen.raumreservierung.booking.ScheduleTemplate_;
import de.muenchen.raumreservierung.room.Room_;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class AppointmentSpecificationBuilder {

    private AppointmentSpecificationBuilder() {
    }

    public static <T extends Appointment> Specification<T> fromFilter(final AppointmentFilterDTO appointmentFilterDTO) {
        final List<Specification<T>> specificationList = new ArrayList<>();

        if (appointmentFilterDTO.bookingId() != null) {
            specificationList.add(filterForBookingId(appointmentFilterDTO.bookingId()));
        }
        if (appointmentFilterDTO.roomIds() != null && !appointmentFilterDTO.roomIds().isEmpty()) {
            specificationList.add(filterForRoomIds(appointmentFilterDTO.roomIds()));
        }
        final OffsetDateTime start = appointmentFilterDTO.startDate();
        if (start != null) {
            specificationList.add(filterForStartDate(start.toLocalDate().atStartOfDay(start.getOffset()).toOffsetDateTime()));
        }
        final OffsetDateTime end = appointmentFilterDTO.endDate();
        if (end != null) {
            specificationList.add(filterForEndDate(end.toLocalDate().atTime(LocalTime.MAX).atZone(end.getOffset()).toOffsetDateTime()));
        }

        return Specification.allOf(specificationList);
    }

    private static <T extends Appointment> Specification<T> filterForRoomIds(final List<UUID> roomIds) {
        return (root, query, cb) -> root.get(Appointment_.booking).get(Booking_.room).get(Room_.id).in(roomIds);
    }

    private static <T extends Appointment> Specification<T> filterForBookingId(final UUID bookingId) {
        return (root, query, cb) -> cb.equal(root.get(Appointment_.booking).get(Booking_.id), bookingId);
    }

    private static <T extends Appointment> Specification<T> filterForStartDate(final OffsetDateTime start) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(Appointment_.schedule).get(ScheduleTemplate_.occupancyStart), start);
    }

    private static <T extends Appointment> Specification<T> filterForEndDate(final OffsetDateTime end) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(Appointment_.schedule).get(ScheduleTemplate_.occupancyEnd), end);
    }

    private static final ZoneId BERLIN = ZoneId.of("Europe/Berlin");

    private static <T extends Appointment> Specification<T> filterForYear(final int year) {
        final OffsetDateTime yearStart = ZonedDateTime.of(year, 1, 1, 0, 0, 0, 0, BERLIN).toOffsetDateTime();
        final OffsetDateTime yearEnd = ZonedDateTime.of(year, 12, 31, 23, 59, 59, 999_999_999, BERLIN).toOffsetDateTime();

        return (root, query, cb) -> cb.and(
                cb.greaterThanOrEqualTo(root.get(Appointment_.schedule).get(ScheduleTemplate_.occupancyStart), yearStart),
                cb.lessThanOrEqualTo(root.get(Appointment_.schedule).get(ScheduleTemplate_.occupancyStart), yearEnd));
    }

    public static <T extends Appointment> Specification<T> forYearAndBookingId(final int year, final UUID bookingId) {
        final List<Specification<T>> specificationList = new ArrayList<>();
        specificationList.add(filterForYear(year));
        specificationList.add(filterForBookingId(bookingId));
        return Specification.allOf(specificationList);
    }
}
