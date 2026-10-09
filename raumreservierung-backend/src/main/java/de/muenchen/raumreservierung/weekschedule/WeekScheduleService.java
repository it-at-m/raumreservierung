package de.muenchen.raumreservierung.weekschedule;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.appointment.AppointmentRepository;
import de.muenchen.raumreservierung.appointment.AppointmentSpecificationBuilder;
import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.room.Room;
import de.muenchen.raumreservierung.room.RoomService;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeekScheduleService {

    private final RoomService roomService;
    private final AppointmentRepository appointmentRepository;
    private final WeekScheduleMapper weekScheduleMapper;
    private final SpringTemplateEngine templateEngine;

    private static final int MAX_BOOKINGS_PER_PAGE = 10;

    @Transactional(readOnly = true)
    public void generateWeekSchedule(final int week, final WeekScheduleCategory category, final OutputStream outputStream) {
        final List<Room> rooms = roomService.findAllActiveByWeekScheduleCategory(category);
        final List<Appointment> appointments = loadAppointments(week, rooms);
        final List<RoomRowExportDto> roomRows = rooms.stream()
                .map(room -> weekScheduleMapper.toRoomRow(room, appointments))
                .toList();

        final Context context = new Context();
        context.setVariable("week", week);
        context.setVariable("days", weekScheduleMapper.toDayHeaders(week));
        context.setVariable("pages", paginate(roomRows));

        final String html = templateEngine.process("weekschedule", context);

        log.debug("row heights: {}", roomRows.stream().map(r -> r.name() + "=" + r.height()).toList());
        try {
            final PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
        } catch (final IOException e) {
            throw new UncheckedIOException("week schedule - PDF could not be generated", e);
        }
    }

    private List<Appointment> loadAppointments(final int week, final List<Room> rooms) {
        if (rooms.isEmpty()) {
            return List.of();
        }

        final LocalDate mondayOfWeek = LocalDate.now()
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(DayOfWeek.MONDAY);

        final OffsetDateTime rangeStart = startOfDay(mondayOfWeek);
        final OffsetDateTime rangeEnd = startOfDay(mondayOfWeek.plusWeeks(1));
        final List<UUID> roomIds = rooms.stream().map(Room::getId).toList();

        final AppointmentFilterDTO filter = new AppointmentFilterDTO(rangeStart, rangeEnd, null, roomIds);

        return appointmentRepository.findAll(
                AppointmentSpecificationBuilder.fromFilter(filter),
                Sort.by("schedule.occupancyStart"));
    }

    private OffsetDateTime startOfDay(final LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
    }

    private List<List<RoomRowExportDto>> paginate(final List<RoomRowExportDto> rows) {
        final List<List<RoomRowExportDto>> pages = new ArrayList<>();
        pages.add(new ArrayList<>());
        int used = 0;

        for (final RoomRowExportDto row : rows) {
            RoomRowExportDto rest = row;

            if (rest.height() > MAX_BOOKINGS_PER_PAGE - used && rest.height() <= MAX_BOOKINGS_PER_PAGE) {
                pages.add(new ArrayList<>());
                used = 0;
            }

            while (rest.height() > MAX_BOOKINGS_PER_PAGE - used) {
                final int free = MAX_BOOKINGS_PER_PAGE - used;
                if (free > 0) {
                    pages.getLast().add(rest.slice(0, free));
                    rest = rest.slice(free, Integer.MAX_VALUE);
                }
                pages.add(new ArrayList<>());
                used = 0;
            }

            pages.getLast().add(rest);
            used += rest.height();
        }
        return pages;
    }

}
