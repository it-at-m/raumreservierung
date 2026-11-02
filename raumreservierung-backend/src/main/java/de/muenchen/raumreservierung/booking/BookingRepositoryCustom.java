package de.muenchen.raumreservierung.booking;

/**
 * Custom persistence operations for bookings.
 */
public interface BookingRepositoryCustom {

    /**
     * Detaches the booking and its loaded person and seating references from the persistence
     * context, so subsequent modifications do not get flushed implicitly.
     *
     * @param booking the booking to detach.
     */
    void detach(Booking booking);
}
