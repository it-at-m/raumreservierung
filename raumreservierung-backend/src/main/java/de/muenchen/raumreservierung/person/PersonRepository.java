package de.muenchen.raumreservierung.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID>, PagingAndSortingRepository<Person, UUID> {
}
