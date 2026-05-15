package de.muenchen.raumreservierung.person;

import de.muenchen.raumreservierung.person.domain.InternalPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InternalPersonRepository extends JpaRepository<InternalPerson, UUID>, JpaSpecificationExecutor<InternalPerson> {

    Optional<InternalPerson> findInternalPersonByOrganisationId(String organisationId);
}
