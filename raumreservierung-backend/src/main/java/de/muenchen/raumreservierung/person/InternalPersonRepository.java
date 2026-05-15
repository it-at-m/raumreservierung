package de.muenchen.raumreservierung.person;

import de.muenchen.raumreservierung.person.domain.InternalPerson;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InternalPersonRepository extends JpaRepository<InternalPerson, UUID>, JpaSpecificationExecutor<InternalPerson> {
}
