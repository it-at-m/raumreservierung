package de.muenchen.raumreservierung.appointment;

import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.export.BookingExportDto;
import de.muenchen.raumreservierung.export.BookingExportPort;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter implementing the {@link BookingExportPort} of the export context.
 * Streams appointments of the requested year and maps them to export rows.
 */
@Service
@RequiredArgsConstructor
public class AppointmentExportAdapter implements BookingExportPort {

    private final AppointmentService appointmentService;
    private final ExportMapper exportMapper;

    @Override
    @Transactional(readOnly = true)
    public Stream<BookingExportDto> streamBookingsForYear(final int year) {
        final OffsetDateTime startDate = OffsetDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        final OffsetDateTime endDate = OffsetDateTime.of(year, 12, 31, 23, 59, 59, 999_999_999, ZoneOffset.UTC);

        final AppointmentFilterDTO appFilter = new AppointmentFilterDTO(startDate, endDate, null, null);

        return appointmentService.streamAppointmentsByFilter(appFilter)
                .map(exportMapper::toExportDto);
    }
}
