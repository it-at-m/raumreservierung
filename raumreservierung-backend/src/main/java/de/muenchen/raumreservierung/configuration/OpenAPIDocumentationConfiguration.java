package de.muenchen.raumreservierung.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenAPIDocumentationConfiguration {

    private final OpenAPIProperties openAPIProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(openAPIProperties.getName()).description(openAPIProperties.getDescription()).version(openAPIProperties.getVersion()));
    }

    @Bean
    public OpenApiCustomizer customizeBookingStatusParameter() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().values().forEach(pathItem -> {
                    if (pathItem.getGet() != null && pathItem.getGet().getParameters() != null) {
                        pathItem.getGet().getParameters().stream()
                                .filter(parameter -> "status".equals(parameter.getName()) && "query".equals(parameter.getIn()))
                                .forEach(parameter -> {
                                    parameter.setStyle(Parameter.StyleEnum.FORM);
                                    parameter.setExplode(Boolean.FALSE);
                                });
                    }
                });
            }
        };
    }
}
