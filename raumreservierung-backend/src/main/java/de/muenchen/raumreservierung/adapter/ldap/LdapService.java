package de.muenchen.raumreservierung.adapter.ldap;

import de.muenchen.raumreservierung.person.dto.PersonFilterDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface LdapService {

    Optional<LdapPersonDto> getPersonByObjectID(String objectID);
    List<LdapPersonDto> searchPersonByFilter(PersonFilterDto ldapPersonFilterDto, Pageable pageable);
}
