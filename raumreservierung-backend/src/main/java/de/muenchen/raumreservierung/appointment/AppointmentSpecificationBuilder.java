package de.muenchen.raumreservierung.appointment;

import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.booking.BookingStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

public final class AppointmentSpecificationBuilder {

    private AppointmentSpecificationBuilder() {
    }

    public static <T extends Appointment> Specification<T> fromFilter(final AppointmentFilterDTO appointmentFilterDTO) {
        return fromFilterWithStatus(appointmentFilterDTO, null);
    }

    public static <T extends Appointment> Specification<T> fromFilterWithStatus(final AppointmentFilterDTO appointmentFilterDTO, final BookingStatus status) {
        return Specification.allOf(
                commonSpecifications(appointmentFilterDTO, status),
                AppointmentSpecifications.filterForOccupancyStartOnOrAfter(appointmentFilterDTO.startDate()),
                AppointmentSpecifications.filterForOccupancyEndOnOrBefore(appointmentFilterDTO.endDate())
        );
    }

    public static <T extends Appointment> Specification<T> fromFilterForOverlappingOccupancy(final AppointmentFilterDTO appointmentFilterDTO) {
        return fromFilterWithStatusForOverlappingOccupancy(appointmentFilterDTO, null);
    }

    public static <T extends Appointment> Specification<T> fromFilterWithStatusForOverlappingOccupancy(final AppointmentFilterDTO appointmentFilterDTO, final BookingStatus status) {
        return Specification.allOf(
                commonSpecifications(appointmentFilterDTO, status),
                AppointmentSpecifications.filterForOccupancyEndOnOrAfter(appointmentFilterDTO.startDate()),
                AppointmentSpecifications.filterForOccupancyStartOnOrBefore(appointmentFilterDTO.endDate())
        );
    }

    public static <T extends Appointment> Specification<T> forFutureSeatingTypeUsage(final UUID seatingTypeId, final UUID roomId) {
        final List<Specification<T>> specificationList = new ArrayList<>();

        specificationList.add(AppointmentSpecifications.filterForSeatingTypeId(seatingTypeId));
        specificationList.add(AppointmentSpecifications.filterForOccupancyEndAfter(OffsetDateTime.now()));
        specificationList.add(AppointmentSpecifications.filterExcludingStatus(BookingStatus.CANCELED, BookingStatus.UNFEASIBLE, BookingStatus.NEW));
        specificationList.add(AppointmentSpecifications.filterForRoomId(roomId));

        return Specification.allOf(specificationList);
    }

    private static <T extends Appointment> Specification<T> commonSpecifications(final AppointmentFilterDTO filter, final BookingStatus status) {
        return Specification.allOf(
                AppointmentSpecifications.filterForBookingId(filter.bookingId()),
                AppointmentSpecifications.filterForRoomIds(filter.roomIds()),
                AppointmentSpecifications.filterForStatus(status)
        );
    }

    public static <T extends Appointment> Specification<T> filterForFutureRoomUsage(final UUID roomId) {
        return Specification.allOf(
                AppointmentSpecifications.filterForRoomIds(List.of(roomId)),
                AppointmentSpecifications.filterForOccupancyEndAfter(OffsetDateTime.now()),
                AppointmentSpecifications.filterExcludingStatus(BookingStatus.CANCELED, BookingStatus.UNFEASIBLE, BookingStatus.NEW));
    }

}
