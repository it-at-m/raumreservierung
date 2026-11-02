package de.muenchen.raumreservierung.booking;

/**
 * Port for notifying involved persons about booking status changes.
 * Implemented by the notification context.
 */
public interface BookingNotificationPort {

    /**
     * Sends a notification mail about a booking status change.
     *
     * @param booking the booking whose status changed.
     * @param oldStatus the status before the change.
     */
    void sendStatusNotificationMail(Booking booking, BookingStatus oldStatus);
}
