package de.muenchen.raumreservierung.export;

import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.muenchen.raumreservierung.appointment.Appointment;
import de.muenchen.raumreservierung.appointment.AppointmentRepository;
import de.muenchen.raumreservierung.appointment.AppointmentSpecificationBuilder;
import de.muenchen.raumreservierung.appointment.dto.AppointmentFilterDTO;
import de.muenchen.raumreservierung.configuration.ExportProperties;
import de.muenchen.raumreservierung.security.Authorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvExportService {

    private final AppointmentRepository appointmentRepository;
    private final ExportMapper exportMapper;
    private final ExportProperties exportProperties;

    /**
     * Fetches appointments by filter object.
     *
     * @param filter object.
     * @return a stream of all found appointments.
     */
    private Stream<Appointment> streamAppointmentsByFilter(AppointmentFilterDTO filter) {
        Specification<Appointment> appointmentSpecification = AppointmentSpecificationBuilder.fromFilter(filter);
        return appointmentRepository.findBy(appointmentSpecification, FluentQuery.FetchableFluentQuery::stream);
    }

    /**
     * Fills the output stream with a csv filled with bookings from appointments.
     *
     * @param year         to create the report
     * @param outputStream to fill the csv into.
     */
    @Transactional(readOnly = true)
    @PreAuthorize(Authorities.BOOKING_READ)
    public void exportBookingsAsCsv(int year, OutputStream outputStream) {
        log.debug("Creating export for calendar year {}", year);

        OffsetDateTime startDate = OffsetDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(year, 12, 31, 23, 59, 59, 999999999, ZoneOffset.UTC);

        AppointmentFilterDTO appFilter = new AppointmentFilterDTO(startDate, endDate, null, null);

        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(BookingExportDto.class)
                .withHeader()
                .withColumnSeparator(exportProperties.getCsvColumnSeparator());

        try (Stream<Appointment> appointmentStream = streamAppointmentsByFilter(appFilter);
             SequenceWriter seqWriter = mapper.writer(schema).writeValues(outputStream)) {

            for (Appointment appointment : (Iterable<Appointment>) appointmentStream::iterator) {
                seqWriter.write(exportMapper.toExportDto(appointment));
            }

        } catch (IOException e) {
            throw new RuntimeException(String.format("Fehler beim Erstellen des CSV-Exports für das Jahr %s", year), e);
        }
    }
}
