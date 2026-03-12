package de.muenchen.raumreservierung.person;

import de.muenchen.raumreservierung.person.dto.PersonFilterDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PersonSpecificationBuilder {

    private PersonSpecificationBuilder() {
    }


    public static <T extends Person> Specification<T> fromFilter(final PersonFilterDto personFilterDto) {
        List<Specification<T>> specificationList = new ArrayList<>();

        if (personFilterDto.searchName() != null && !personFilterDto.searchName().isBlank()) {
            specificationList.add(hasNameLike(personFilterDto.searchName()));
        }

        return Specification.allOf(specificationList);
    }


    private static <T extends Person> Specification<T> hasNameLike(String searchName) {
        return ((root, query, criteriaBuilder) -> {
            String searchPattern = "%" + searchName.toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get(Person_.name)), searchPattern);
        });
    }

}
