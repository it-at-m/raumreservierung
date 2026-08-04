package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.security.Roles;
import java.util.List;
import lombok.Getter;

@Getter
public enum BookingStatus {
    NEW(10),
    ROOM_CHANGED(20),
    ORGANIZER_CHANGED(30),
    ROOM_APPROVED(40),
    COORDINATION_NEEDED(50),
    ORGANIZER_APPROVED(60),
    UNFEASIBLE(70),
    CANCELED(80);

    private final int sortOrder;

    BookingStatus(final int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<StateTransition> getTransitions() {
        return switch (this) {
        case NEW -> List.of(
                new StateTransition(ROOM_APPROVED, Roles.RAUM_BUCHUNG),
                new StateTransition(UNFEASIBLE, Roles.RAUM_BUCHUNG),
                new StateTransition(CANCELED, Roles.ANWENDER),
                new StateTransition(NEW, Roles.ANWENDER));
        case ORGANIZER_CHANGED -> List.of(
                new StateTransition(ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                new StateTransition(COORDINATION_NEEDED, Roles.TERMIN_ORGANISATOR),
                new StateTransition(ROOM_CHANGED, Roles.ANWENDER),
                new StateTransition(CANCELED, Roles.ANWENDER),
                new StateTransition(ORGANIZER_CHANGED, Roles.ANWENDER));
        case ROOM_APPROVED -> List.of(
                new StateTransition(ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                new StateTransition(COORDINATION_NEEDED, Roles.TERMIN_ORGANISATOR),
                new StateTransition(ROOM_CHANGED, Roles.ANWENDER),
                new StateTransition(CANCELED, Roles.ANWENDER),
                new StateTransition(ROOM_APPROVED, Roles.ANWENDER));
        case ROOM_CHANGED -> List.of(
                new StateTransition(ORGANIZER_CHANGED, Roles.RAUM_BUCHUNG),
                new StateTransition(UNFEASIBLE, Roles.RAUM_BUCHUNG),
                new StateTransition(CANCELED, Roles.ANWENDER),
                new StateTransition(ROOM_CHANGED, Roles.ANWENDER));
        case COORDINATION_NEEDED -> List.of(
                new StateTransition(UNFEASIBLE, Roles.RAUM_BUCHUNG),
                new StateTransition(ORGANIZER_APPROVED, Roles.TERMIN_ORGANISATOR),
                new StateTransition(CANCELED, Roles.ANWENDER),
                new StateTransition(ROOM_CHANGED, Roles.ANWENDER),
                new StateTransition(COORDINATION_NEEDED, Roles.ANWENDER));
        case ORGANIZER_APPROVED -> List.of(
                new StateTransition(ROOM_CHANGED, Roles.ANWENDER),
                new StateTransition(CANCELED, Roles.ANWENDER),
                new StateTransition(ORGANIZER_APPROVED, Roles.ANWENDER));
        case UNFEASIBLE -> List.of(new StateTransition(UNFEASIBLE, Roles.ANWENDER));
        case CANCELED -> List.of(new StateTransition(CANCELED, Roles.ANWENDER));
        };
    }
}
