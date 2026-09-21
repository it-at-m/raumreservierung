package de.muenchen.raumreservierung.notification;

import org.springframework.beans.factory.annotation.Value;

public class MailConfiguration {
    @Value("${mail.domain}")
    private String domain;

    @Value("${mail.environment}")
    private String environment;
}
