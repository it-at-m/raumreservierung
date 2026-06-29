package de.muenchen.raumreservierung.booking;

public record StateTransition(
        BookingStatus targetStatus,
        String requiredRole) {
}
