package de.muenchen.raumreservierung.adapter.ldap;

import java.util.List;
import java.util.Optional;

import de.muenchen.raumreservierung.person.dto.PersonFilterDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActiveDirectoryServiceImpl implements LdapService {

    private static final String LHM_OBJECT_ID_KEY = "lhmObjectID";

    private final LdapTemplate ldapTemplate;

    @Override
    public Optional<LdapPersonDto> getPersonByObjectID(final String objectID) {
        final ContainerCriteria conditionCriteria = LdapQueryBuilder.query()
                .searchScope(SearchScope.SUBTREE)
                .attributes(LdapAttributes.GIVEN_NAME, LdapAttributes.SN, LdapAttributes.TELEPHONE_NUMBER, LdapAttributes.MAIL, LdapAttributes.LHM_OBJECT_ID,
                        LdapAttributes.ORGANISATIONAL_UNIT)
                .where("objectClass").is("user") // this is only possible while using Active Directories
                .and(LHM_OBJECT_ID_KEY).is(objectID);

        final List<LdapPersonDto> results = ldapTemplate.search(conditionCriteria, new LdapPersonDtoMapper());

        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public List<LdapPersonDto> searchPersonByFilter(final PersonFilterDto ldapPersonFilterDto, final Pageable pageable) {
        if (ldapPersonFilterDto == null || ldapPersonFilterDto.searchName() == null || ldapPersonFilterDto.searchName().isBlank()) {
            return List.of();
        }

        String escapedSearchText = LdapEncoder.filterEncode(ldapPersonFilterDto.searchName().trim());
        String ldapFilter = String.format("(&(objectClass=user)(|(givenName=*%1$s*)(sn=*%1$s*)(cn=*%1$s*)(displayName=*%1$s*)))", escapedSearchText);

        LdapQuery query = LdapQueryBuilder.query()
                .searchScope(SearchScope.SUBTREE)
                .attributes(LdapAttributes.GIVEN_NAME, LdapAttributes.SN, LdapAttributes.TELEPHONE_NUMBER, LdapAttributes.MAIL, LdapAttributes.LHM_OBJECT_ID,
                        LdapAttributes.ORGANISATIONAL_UNIT
                )
                .countLimit(pageable.getPageSize())
                .filter(ldapFilter);

        return ldapTemplate.search(query, new LdapPersonDtoMapper());
    }
}
