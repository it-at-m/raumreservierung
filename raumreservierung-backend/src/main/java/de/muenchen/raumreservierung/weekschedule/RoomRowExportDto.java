package de.muenchen.raumreservierung.weekschedule;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;

@SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "DTOs are simple data carriers")
public record RoomRowExportDto(String name, String number, List<List<BookingMinimalExportDto>> cells) {
}
