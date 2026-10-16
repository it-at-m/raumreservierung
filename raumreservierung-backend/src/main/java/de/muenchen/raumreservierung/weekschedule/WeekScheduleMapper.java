package de.muenchen.raumreservierung.weekschedule;

import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.BookingType;
import de.muenchen.raumreservierung.booking.ScheduleTemplate;
import de.muenchen.raumreservierung.room.Room;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.IsoFields;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface WeekScheduleMapper {
    DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.");
    int MAX_TITLE_LENGTH = 50;

    @Mapping(target = "occupancyTime", source = "schedule", qualifiedByName = "occupancyTime")
    @Mapping(target = "appointmentTime", source = "schedule", qualifiedByName = "appointmentTime")
    @Mapping(target = "title", source = "booking", qualifiedByName = "prefixType")
    BookingMinimalExportDto toBookingMinimalDto(Appointment appointment);

    @Named("occupancyTime")
    default String occupancyTime(final ScheduleTemplate schedule) {
        return String.format("%s - %s",
                schedule.occupancyStart().format(TIME_FORMAT),
                schedule.occupancyEnd().format(TIME_FORMAT));
    }

    @Named("appointmentTime")
    default String appointmentTime(final ScheduleTemplate schedule) {
        return String.format("(%s - %s)",
                schedule.appointmentStart().format(TIME_FORMAT),
                schedule.appointmentEnd().format(TIME_FORMAT));
    }

    @Named("prefixType")
    default String prefixType(final Booking booking) {
        final String title = booking.getTitle() == null ? "" : booking.getTitle();
        final BookingType type = booking.getBookingType();

        final String prefix = type == null ? "" : switch (type) {
        case FREE -> "Frei: ";
        case SERVICE -> "Service: ";
        default -> "";
        };

        return truncateTitle(prefix + title);
    }

    default String truncateTitle(final String title) {
        if (title == null || title.length() <= MAX_TITLE_LENGTH) {
            return title;
        }
        return title.substring(0, MAX_TITLE_LENGTH - 1) + "...";
    }

    default List<DayHeaderExportDto> toDayHeaders(final int week) {
        final LocalDate dateInWeek = LocalDate.now()
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week);

        return Arrays.stream(DayOfWeek.values())
                .map(dateInWeek::with)
                .map(day -> new DayHeaderExportDto(
                        day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.GERMAN),
                        day.format(DATE_FORMAT)))
                .toList();
    }

    default RoomRowExportDto toRoomRow(final Room room, final List<Appointment> appointments) {
        final List<List<BookingMinimalExportDto>> cells = Arrays.stream(DayOfWeek.values())
                .map(day -> appointments.stream()
                        .filter(a -> a.getBooking().getRoom().getId().equals(room.getId()))
                        .filter(a -> dayOf(a) == day)
                        .map(this::toBookingMinimalDto)
                        .toList())
                .toList();

        return new RoomRowExportDto(room.getName(), room.getNumber(), cells);
    }

    default DayOfWeek dayOf(final Appointment appointment) {
        return appointment.getSchedule().occupancyStart()
                .atZoneSameInstant(ZoneId.systemDefault())
                .getDayOfWeek();
    }
}
