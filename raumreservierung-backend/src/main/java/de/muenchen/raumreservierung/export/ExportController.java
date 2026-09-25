package de.muenchen.raumreservierung.export;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @GetMapping(value = "/bookings/csv", produces = "text/csv")
    @ResponseStatus(HttpStatus.OK)
    public StreamingResponseBody exportBookingsCsv(@RequestParam int year, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=bookings_export_%s.csv", year));

        return outputStream -> {
            csvExportService.exportBookingsAsCsv(year, outputStream);
        };
    }

}
