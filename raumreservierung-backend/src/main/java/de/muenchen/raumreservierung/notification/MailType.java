package de.muenchen.raumreservierung.notification;

import lombok.Getter;

@Getter
public enum MailType {
    ROOM_CHANGED("room-changed-subject-template.ftl", "room-changed-template.ftl"),
    CANCEL("cancel-subject-template.ftl", "cancel-template.ftl"),
    APPROVAL("approval-subject-template.ftl", "approval-template.ftl"),
    IN_PROGRESS("in-progress-subject-template.ftl", "in-progress-template.ftl"),
    UNFEASIBLE("unfeasible-subject-template.ftl", "unfeasible-template.ftl");

    private final String subjectTemplate;
    private final String templateFileName;

    MailType(final String subjectTemplate, final String templateFileName) {
        this.subjectTemplate = subjectTemplate;
        this.templateFileName = templateFileName;
    }
}
