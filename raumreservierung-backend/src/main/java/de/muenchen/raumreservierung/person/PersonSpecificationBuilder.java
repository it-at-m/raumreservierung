package de.muenchen.raumreservierung.person;

import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.person.domain.Person_;
import de.muenchen.raumreservierung.person.dto.PersonFilterDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PersonSpecificationBuilder {

    private PersonSpecificationBuilder() {
    }

    public static <T extends Person> Specification<T> fromFilter(final PersonFilterDto personFilterDto) {
        final List<Specification<T>> specificationList = new ArrayList<>();

        if (personFilterDto.searchName() != null && !personFilterDto.searchName().isBlank()) {
            specificationList.add(filterForFirstName(personFilterDto.searchName()));
            specificationList.add(filterForLastName(personFilterDto.searchName()));
            specificationList.add(filterForEmail(personFilterDto.searchName()));
        }

        return Specification.anyOf(specificationList);
    }

    private static String toLikePattern(final String searchName) {
        return "%" + searchName.toLowerCase(Locale.GERMAN) + "%";
    }

    private static <T extends Person> Specification<T> filterForFirstName(final String searchName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(Person_.firstName)), toLikePattern(searchName));
    }

    private static <T extends Person> Specification<T> filterForLastName(final String searchName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(Person_.lastName)), toLikePattern(searchName));
    }

    private static <T extends Person> Specification<T> filterForEmail(final String searchName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(Person_.email)), toLikePattern(searchName));
    }
}
