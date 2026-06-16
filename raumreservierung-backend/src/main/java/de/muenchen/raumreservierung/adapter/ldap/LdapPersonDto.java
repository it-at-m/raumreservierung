package de.muenchen.raumreservierung.adapter.ldap;

public record LdapPersonDto(
        String firstName,
        String lastName,
        String telefonNumber,
        String email,
        String organisationId,
        String organisationUnit) {
}
