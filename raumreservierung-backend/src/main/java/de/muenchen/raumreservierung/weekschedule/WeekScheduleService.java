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

    @Transactional(readOnly = true)
    public void generateWeekSchedule(final LocalDate date, final WeekScheduleCategory category, final OutputStream outputStream) {
        final LocalDate monday = date.with(DayOfWeek.MONDAY);
        final List<Room> rooms = roomService.findAllActiveByWeekScheduleCategory(category);
        final List<Appointment> appointments = loadAppointments(monday, rooms);
        final List<RoomRowExportDto> roomRows = rooms.stream()
                .map(room -> weekScheduleMapper.toRoomRow(room, appointments))
                .toList();

        final Context context = new Context();
        context.setVariable("model", weekScheduleMapper.toView(monday, category, roomRows));
        final String html = templateEngine.process("weekschedule", context);

        try {
            final PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
        } catch (final IOException e) {
            throw new UncheckedIOException("week schedule - PDF could not be generated", e);
        }
    }

    private List<Appointment> loadAppointments(final LocalDate mondayOfWeek, final List<Room> rooms) {
        if (rooms.isEmpty()) {
            return List.of();
        }

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

}
