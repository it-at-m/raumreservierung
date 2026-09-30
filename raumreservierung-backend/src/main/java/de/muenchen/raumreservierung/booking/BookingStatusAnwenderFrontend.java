package de.muenchen.raumreservierung.booking;

import lombok.Getter;

@Getter
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
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
