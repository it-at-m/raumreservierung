package de.muenchen.raumreservierung.configuration;

import org.springframework.beans.factory.annotation.Value;

public class MailInfoConfiguration {
    @Value("${refarch.mail.appdomain}")
    private String domain;

    @Value("${refarch.mail.environment}")
    private String environment;
}
