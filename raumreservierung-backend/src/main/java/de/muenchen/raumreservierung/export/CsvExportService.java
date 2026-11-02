package de.muenchen.raumreservierung.export;

import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.muenchen.raumreservierung.configuration.ExportProperties;
import de.muenchen.raumreservierung.security.Authorities;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvExportService {

    private final BookingExportPort bookingExportPort;
    private final ExportProperties exportProperties;

    /**
     * Fills the output stream with a csv filled with bookings from appointments.
     *
     * @param year to create the report
     * @param outputStream to fill the csv into.
     */
    @Transactional(readOnly = true)
    @PreAuthorize(Authorities.BOOKING_READ)
    public void exportBookingsAsCsv(final int year, final OutputStream outputStream) {
        log.debug("Creating export for calendar year {}", year);

        final CsvMapper mapper = new CsvMapper();
        final CsvSchema schema = mapper.schemaFor(BookingExportDto.class)
                .withHeader()
                .withColumnSeparator(exportProperties.getCsvColumnSeparator());

        try {
            // Excel does Microsoft things and does not recognize the file-header provided via the download.
            // Therefore, we write utf-8 into the BOM
            outputStream.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
        } catch (IOException e) {
            throw new UncheckedIOException("Error while writing the UTF-8 BOM", e);
        }

        try (Stream<BookingExportDto> exportStream = bookingExportPort.streamBookingsForYear(year);
                SequenceWriter seqWriter = mapper.writer(schema).writeValues(outputStream)) {

            for (final BookingExportDto row : (Iterable<BookingExportDto>) exportStream::iterator) {
                seqWriter.write(row);
            }

        } catch (IOException e) {
            throw new RuntimeException(String.format("Eror on creation of the csv export for the year %s", year), e);
        }
    }
}
