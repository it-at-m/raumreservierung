package de.muenchen.raumreservierung.weekschedule;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;

@SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "DTOs are simple data carriers")
public record RoomRowExportDto(
        String name,
        String number,
        List<List<LaneCellDto>> lanes) {

    public int rowCount() {
        return Math.max(1, lanes.size());
    }
}
