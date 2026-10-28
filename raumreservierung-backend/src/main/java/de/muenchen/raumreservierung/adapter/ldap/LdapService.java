package de.muenchen.raumreservierung.adapter.ldap;

import de.muenchen.raumreservierung.person.dto.PersonFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LdapService {

    Optional<LdapPersonDto> getPersonByObjectID(String objectID);
    List<LdapPersonDto> searchPersonByFilter(PersonFilterDto ldapPersonFilterDto, Pageable pageable);
}
