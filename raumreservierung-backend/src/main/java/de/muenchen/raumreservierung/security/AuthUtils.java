package de.muenchen.raumreservierung.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Utilities for authentication data.
 */
public final class AuthUtils {

    public static final String NAME_UNAUTHENTICATED_USER = "unauthenticated";

    private static final String TOKEN_USER_NAME = "preferred_username";
    private static final String TOKEN_ORGANISATION_ID = "lhmObjectID";

    private AuthUtils() {
    }

    /**
     * Extracts the preferred_username from the existing Spring Security Context via
     * {@link SecurityContextHolder}.
     *
     * @return the username or an "unauthenticated" if no {@link Authentication} exists
     */
    public static String getUsername() {
        return getClaimOrFallback(TOKEN_USER_NAME);
    }

    /**
     * Extracts the organisationID from the existing Spring Security Context via
     * {@link SecurityContextHolder}.
     *
     * @return the organisationID or an "unauthenticated" if no {@link Authentication} exists
     */
    public static String getOrganisationId() {
        return getClaimOrFallback(TOKEN_ORGANISATION_ID);
    }

    private static String getClaimOrFallback(final String tokenName) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return NAME_UNAUTHENTICATED_USER;
        }

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return (String) jwtAuthenticationToken.getTokenAttributes().getOrDefault(tokenName, null);
        }

        if (authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            return usernamePasswordAuthenticationToken.getName();
        }

        return NAME_UNAUTHENTICATED_USER;
    }

}
