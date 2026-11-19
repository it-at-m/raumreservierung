package de.muenchen.raumreservierung.booking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingType {
    FREE("Frei"),
    SERVICE("Service"),
    DEFAULT("");

    private final String label;
}
