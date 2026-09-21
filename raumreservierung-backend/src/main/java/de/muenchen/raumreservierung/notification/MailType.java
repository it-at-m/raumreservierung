package de.muenchen.raumreservierung.notification;

import lombok.Getter;

@Getter
public enum MailType {
    ROOM_CHANGED("unfeasible-subject-template.ftl", "unfeasible-template.ftl"),
    CANCEL("unfeasible-subject-template.ftl", "unfeasible-template.ftl"),
    APPROVAL("unfeasible-subject-template.ftl", "unfeasible-template.ftl"),
    IN_PROGRESS("unfeasible-subject-template.ftl", "unfeasible-template.ftl"),
    UNFEASIBLE("unfeasible-subject-template.ftl", "unfeasible-template.ftl");

    private final String subjectTemplate;
    private final String templateFileName;

    MailType(final String subjectTemplate, final String templateFileName) {
        this.subjectTemplate = subjectTemplate;
        this.templateFileName = templateFileName;
    }
}
