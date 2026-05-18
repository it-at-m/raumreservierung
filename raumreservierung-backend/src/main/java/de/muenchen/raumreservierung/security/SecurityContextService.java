package de.muenchen.raumreservierung.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SecurityContextService {

    private final RoleHierarchy roleHierarchy;

    public String getCurrentOID() {
        return AuthUtils.getOrganisationId();
    }

    /**
     * Checks if the currently authenticated user lacks a specific authority.
     * This method takes the configured role hierarchy into account.
     *
     * @param role The name of the authority/role to check for.
     * @return true if the user is not authenticated or does not have the authority.
     */
    public boolean hasAuthority(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities())
                .stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

}
