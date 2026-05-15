package de.muenchen.raumreservierung.configuration;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "raumreservierung.scheduling")
@Data
@Validated
public class SchedulingProperties {

    @NotNull private int maxExtPersonAgeMonths;

    @NotNull private String extPersonCleanup;

}
