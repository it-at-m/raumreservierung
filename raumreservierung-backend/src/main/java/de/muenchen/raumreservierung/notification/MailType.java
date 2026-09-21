package de.muenchen.raumreservierung.notification;

import lombok.Getter;

@Getter
public enum MailType {
    ROOM_CHANGED("",""),
    CANCEL("",""),
    APPROVAL("",""),
    IN_PROGRESS("",""),
    UNFEASIBLE("","");

    private final String subjectTemplate;
    private final String templateFileName;

    MailType(final String subjectTemplate, final String templateFileName) {
        this.subjectTemplate = subjectTemplate;
        this.templateFileName = templateFileName;
    }
}
