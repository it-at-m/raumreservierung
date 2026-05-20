package de.muenchen.raumreservierung.booking;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockJwt> {
    @Override
    public SecurityContext createSecurityContext(WithMockJwt annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("lhmObjectID", annotation.lhmObjectID())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Collection<SimpleGrantedAuthority> authorities = Arrays.stream(annotation.authorities())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Authentication auth = new JwtAuthenticationToken(jwt, authorities);
        context.setAuthentication(auth);

        return context;
    }
}
