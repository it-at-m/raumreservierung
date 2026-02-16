package de.muenchen.raumreservierung.configuration.security;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configure how authorities are resolved.
 * Either {@link KeycloakRolesAuthoritiesConverter} or with profile "userinfo-authorities"
 * deprecated {@link UserInfoAuthoritiesConverter}.
 */
@Configuration
@Profile("!no-security")
public class AuthoritiesConverterConfiguration {
    @Bean
    @Profile("!keycloak-permissions")
    public KeycloakRolesAuthoritiesConverter keycloakRolesAuthoritiesConverter(final SecurityProperties securityProperties) {
        return new KeycloakRolesAuthoritiesConverter(securityProperties);
    }

    @Bean
    @Profile("keycloak-permissions")
    public KeycloakPermissionsAuthoritiesConverter keycloakPermissionsAuthoritiesConverter(
            final SecurityProperties securityProperties, final RestTemplateBuilder restTemplateBuilder) {
        return new KeycloakPermissionsAuthoritiesConverter(securityProperties, restTemplateBuilder);
    }
}
