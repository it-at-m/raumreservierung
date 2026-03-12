package de.muenchen.raumreservierung.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InternalPersonRepository extends JpaRepository<InternalPerson, UUID>, JpaSpecificationExecutor<InternalPerson> {
}
