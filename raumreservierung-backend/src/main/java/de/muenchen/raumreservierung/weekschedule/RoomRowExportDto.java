package de.muenchen.raumreservierung.weekschedule;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;

@SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "DTOs are simple data carriers")
public record RoomRowExportDto(String name, String number, List<List<BookingMinimalExportDto>> cells) {
    public int height() {
        return Math.max(1, cells.stream().mapToInt(List::size).max().orElse(0));
    }

    public RoomRowExportDto slice(final int from, final int to) {
        final List<List<BookingMinimalExportDto>> sliced = cells.stream()
                .map(day -> day.subList(Math.min(from, day.size()), Math.min(to, day.size())))
                .toList();
        return new RoomRowExportDto(name, number, sliced);
    }
}
