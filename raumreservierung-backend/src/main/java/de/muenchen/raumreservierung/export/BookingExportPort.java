package de.muenchen.raumreservierung.export;

import java.util.stream.Stream;

/**
 * Port for streaming booking export data for a given calendar year.
 * Implemented by the appointment context.
 */
public interface BookingExportPort {

    /**
     * Streams all export rows of bookings whose appointments lie within the given year.
     *
     * @param year the calendar year to export.
     * @return a stream of export rows.
     */
    Stream<BookingExportDto> streamBookingsForYear(int year);
}
