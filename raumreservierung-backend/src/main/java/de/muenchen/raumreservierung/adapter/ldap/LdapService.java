package de.muenchen.raumreservierung.adapter.ldap;

import java.util.Optional;

public interface LdapService {

    Optional<LdapPersonDto> getPersonByObjectID(String objectID);

}
