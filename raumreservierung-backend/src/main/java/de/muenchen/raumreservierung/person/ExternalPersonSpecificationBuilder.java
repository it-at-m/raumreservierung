package de.muenchen.raumreservierung.person;

import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import de.muenchen.raumreservierung.person.domain.ExternalPerson_;
import de.muenchen.raumreservierung.person.dto.PersonFilterDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ExternalPersonSpecificationBuilder {

    private ExternalPersonSpecificationBuilder() {
    }

    public static <T extends ExternalPerson> Specification<T> fromFilter(final PersonFilterDto personFilterDto) {
        final List<Specification<T>> specificationList = new ArrayList<>();

        if (personFilterDto.searchName() != null && !personFilterDto.searchName().isBlank()) {
            specificationList.add(filterForCompany(personFilterDto.searchName()));
        }

        return Specification.anyOf(specificationList);
    }

    private static <T extends ExternalPerson> Specification<T> filterForCompany(final String searchName) {
        return (root, query, cb) -> {
            final String searchString = "%" + searchName.toLowerCase(Locale.GERMAN) + "%";
            return cb.like(cb.lower(root.get(ExternalPerson_.company)), searchString);
        };
    }

}
