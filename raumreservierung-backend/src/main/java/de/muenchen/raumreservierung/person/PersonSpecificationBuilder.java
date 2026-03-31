package de.muenchen.raumreservierung.person;

import de.muenchen.raumreservierung.person.dto.PersonFilterDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public final class PersonSpecificationBuilder {

    private PersonSpecificationBuilder() {
    }

    public static <T extends Person> Specification<T> fromFilter(final PersonFilterDto personFilterDto) {
        final List<Specification<T>> specificationList = new ArrayList<>();

        if (personFilterDto.searchName() != null && !personFilterDto.searchName().isBlank()) {
            specificationList.add(filterForName(personFilterDto.searchName()));
        }

        return Specification.allOf(specificationList);
    }

    private static <T extends Person> Specification<T> filterForName(final String searchName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(Person_.lastName)), "%" + searchName.toLowerCase(Locale.GERMAN) + "%");
    }

}
