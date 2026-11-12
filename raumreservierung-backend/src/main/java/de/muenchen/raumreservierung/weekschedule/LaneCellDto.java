package de.muenchen.raumreservierung.weekschedule;

import java.util.List;

public record LaneCellDto(
        int colspan,
        boolean multiDay,
        boolean continuesBefore,
        boolean continuesAfter,
        List<BookingMinimalExportDto> appointments) {

    public boolean isEmpty() {
        return appointments.isEmpty();
    }
}