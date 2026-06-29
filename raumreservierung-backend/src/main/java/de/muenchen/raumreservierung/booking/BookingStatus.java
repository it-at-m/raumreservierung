package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.security.Roles;
import java.util.List;

public enum BookingStatus {
    NEW,
    ROOM_APPROVED,
    ROOM_CHANGED,
    COORDINATION_NEEDED,
    ORGANIZER_APPROVED,
    ORGANIZER_CHANGED,
    UNFEASIBLE,
    CANCELLED;

    public List<StateTransition> getTransitions() {
        return switch (this) {
        case NEW -> List.of(
                new StateTransition(ROOM_APPROVED, Roles.RAUM_BUCHUNG),
                new StateTransition(UNFEASIBLE, Roles.RAUM_BUCHUNG),
                new StateTransition(CANCELLED, Roles.ANWENDER),
                new StateTransition(NEW, Roles.ANWENDER));
        case ORGANIZER_CHANGED -> List.of(
                new StateTransition(ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                new StateTransition(COORDINATION_NEEDED, Roles.TERMIN_ORGANISATOR),
                new StateTransition(ROOM_CHANGED, Roles.ANWENDER),
                new StateTransition(CANCELLED, Roles.ANWENDER),
                new StateTransition(ORGANIZER_CHANGED, Roles.ANWENDER));
        case ROOM_APPROVED -> List.of(
                new StateTransition(ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                new StateTransition(COORDINATION_NEEDED, Roles.TERMIN_ORGANISATOR),
                new StateTransition(ROOM_CHANGED, Roles.ANWENDER),
                new StateTransition(CANCELLED, Roles.ANWENDER),
                new StateTransition(ROOM_APPROVED, Roles.ANWENDER));
        case ROOM_CHANGED -> List.of(
                new StateTransition(ORGANIZER_CHANGED, Roles.RAUM_BUCHUNG),
                new StateTransition(UNFEASIBLE, Roles.RAUM_BUCHUNG),
                new StateTransition(CANCELLED, Roles.ANWENDER),
                new StateTransition(ROOM_CHANGED, Roles.ANWENDER));
        case COORDINATION_NEEDED -> List.of(
                new StateTransition(UNFEASIBLE, Roles.RAUM_BUCHUNG),
                new StateTransition(ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                new StateTransition(CANCELLED, Roles.ANWENDER),
                new StateTransition(ROOM_CHANGED, Roles.ANWENDER),
                new StateTransition(COORDINATION_NEEDED, Roles.ANWENDER));
        case ORGANIZER_APPROVED -> List.of(
                new StateTransition(ROOM_CHANGED, Roles.ANWENDER),
                new StateTransition(CANCELLED, Roles.ANWENDER),
                new StateTransition(ORGANIZER_APPROVED, Roles.ANWENDER));
        case UNFEASIBLE -> List.of(new StateTransition(UNFEASIBLE, Roles.ANWENDER));
        case CANCELLED -> List.of(new StateTransition(CANCELLED, Roles.ANWENDER));
        };
    }
}
