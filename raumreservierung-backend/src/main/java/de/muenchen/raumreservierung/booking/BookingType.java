package de.muenchen.raumreservierung.booking;

import lombok.Getter;

@Getter
public enum BookingType {
    FREE(false),
    SERVICE(true),
    NORMAL(true);

    private final boolean blocking;

    BookingType(final boolean blocking) {
        this.blocking = blocking;
    }
}
