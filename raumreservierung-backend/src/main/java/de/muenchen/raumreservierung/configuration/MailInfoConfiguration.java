package de.muenchen.raumreservierung.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class MailInfoConfiguration {
    @Value("${refarch.mail.appdomain}")
    private String domain;

    @Value("${refarch.mail.environment}")
    private String environment;
}
