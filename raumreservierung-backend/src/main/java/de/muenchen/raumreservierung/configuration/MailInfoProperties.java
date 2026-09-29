package de.muenchen.raumreservierung.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "raumreservierung.mail")
@Data
@Validated
public class MailInfoProperties {
    private String appdomain;
    private String environment;
}
