package de.muenchen.raumreservierung.notification;

import de.muenchen.raumreservierung.booking.BookingStatus;

import java.util.AbstractMap;
import java.util.Map;

public class StatusNotificationTransitionMap {

    public record StatusTransition(BookingStatus from, BookingStatus to){}

    private static final Map<StatusTransition, MailType> transitionMap =  Map.ofEntries(
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.NEW, BookingStatus.ROOM_APPROVED), MailType.IN_PROGRESS),
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.NEW, BookingStatus.CANCELED), MailType.CANCEL),
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.NEW, BookingStatus.UNFEASIBLE), MailType.UNFEASIBLE),

            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.ROOM_CHANGED, BookingStatus.ORGANIZER_CHANGED), MailType.IN_PROGRESS),
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.ROOM_CHANGED, BookingStatus.CANCELED), MailType.CANCEL),
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.ROOM_CHANGED, BookingStatus.UNFEASIBLE), MailType.UNFEASIBLE),

            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.ROOM_APPROVED, BookingStatus.ROOM_CHANGED), MailType.ROOM_CHANGED),
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.ROOM_APPROVED,BookingStatus.ORGANIZER_APPROVED), MailType.APPROVAL),
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.ROOM_APPROVED,BookingStatus.CANCELED), MailType.CANCEL),

            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.ORGANIZER_CHANGED, BookingStatus.ROOM_CHANGED), MailType.ROOM_CHANGED),
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.ORGANIZER_CHANGED,BookingStatus.ORGANIZER_APPROVED), MailType.APPROVAL),
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.ORGANIZER_CHANGED,BookingStatus.CANCELED), MailType.CANCEL),

            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.COORDINATION_NEEDED, BookingStatus.ROOM_CHANGED), MailType.ROOM_CHANGED),
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.COORDINATION_NEEDED,BookingStatus.ORGANIZER_APPROVED), MailType.APPROVAL),
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.COORDINATION_NEEDED,BookingStatus.CANCELED), MailType.CANCEL),
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.COORDINATION_NEEDED,BookingStatus.UNFEASIBLE), MailType.UNFEASIBLE),

            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.ORGANIZER_APPROVED, BookingStatus.ROOM_CHANGED), MailType.ROOM_CHANGED),
            new AbstractMap.SimpleEntry<>(new StatusTransition(BookingStatus.ORGANIZER_APPROVED, BookingStatus.CANCELED), MailType.CANCEL));

    public static MailType getMailTypeForTransition(BookingStatus oldStatus, BookingStatus newStatus) {
        return transitionMap.get(new StatusTransition(oldStatus, newStatus));
    }
}
