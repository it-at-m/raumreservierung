package de.muenchen.raumreservierung.booking;

import de.muenchen.raumreservierung.security.Roles;
import lombok.Getter;

import java.util.List;

@Getter
public enum BookingStatusAnwenderFrontend {
    REQUESTED("requested"),
    IN_PROGRESS("requested"),
    APPROVED("requested"),
    UNFEASIBLE("requested"),
    CANCELED("requested");

    private final String mailTemplateName;

    BookingStatusAnwenderFrontend(final String mailTemplateName) {
        this.mailTemplateName = mailTemplateName;
    }
}
