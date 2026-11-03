package de.muenchen.raumreservierung.configuration;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "raumreservierung.export")
@Data
@Validated
public class ExportProperties {

    @NotNull private char csvColumnSeparator;
}
