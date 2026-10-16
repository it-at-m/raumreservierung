package de.muenchen.raumreservierung.export;

import de.muenchen.raumreservierung.common.BadRequestException;
import de.muenchen.raumreservierung.weekschedule.WeekScheduleCategory;
import de.muenchen.raumreservierung.weekschedule.WeekScheduleService;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/export")
public class ExportController {

    private final CsvExportService csvExportService;
    private final WeekScheduleService weekScheduleService;

    @GetMapping(value = "/bookings/csv", produces = "text/csv; charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    public StreamingResponseBody exportBookingsCsv(@RequestParam final int year, final HttpServletResponse response) {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=bookings_export_%s.csv", year));

        return outputStream -> {
            csvExportService.exportBookingsAsCsv(year, outputStream);
        };
    }

    @GetMapping(value = "/bookings/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public StreamingResponseBody weekScheduleAsPDF(@RequestParam(required = false) final Integer week, @RequestParam final WeekScheduleCategory category,
            final HttpServletResponse response) {
        final int calendarWeek = resolveCalendarWeek(week);
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("inline; filename=dienstplan_kw%s.pdf", calendarWeek));
        return outputStream -> {
            weekScheduleService.generateWeekSchedule(calendarWeek, category, outputStream);
        };
    }

    private int resolveCalendarWeek(final Integer week) {
        if (week == null) {
            return LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        }
        if (week < 1 || week > 53) {
            throw new BadRequestException("Die Kalenderwoche muss zwischen 1 und 53 liegen");
        }
        return week;
    }

}
