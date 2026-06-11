package de.muenchen.raumreservierung.adapter.ldap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LdapServiceImpl implements LdapService {

    private static final String LHM_OBJECT_ID_KEY = "lhmObjectID";

    private final LdapTemplate ldapTemplate;

    @Override
    public Optional<LdapPersonDto> getPersonByObjectID(String objectID) {
        log.debug("Searching AD-USER with lhmObjectID: {}", objectID);

        ContainerCriteria conditionCriteria = LdapQueryBuilder.query()
                .searchScope(SearchScope.SUBTREE)
                //.attributes(LdapAttributes.GIVEN_NAME, LdapAttributes.SURNAME, LdapAttributes.TELEPHONE_NUMBER, LdapAttributes.MAIL, LdapAttributes.LHM_OBJECT_ID, LdapAttributes.ORGANISATIONAL_UNIT)
                .where("objectClass").is("user")
                .and(LHM_OBJECT_ID_KEY).is(objectID);

        List<LdapPersonDto> results = ldapTemplate.search(conditionCriteria, new LdapPersonDtoMapper());

        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }
}
