package de.muenchen.raumreservierung.person;

import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalPersonRepository extends JpaRepository<ExternalPerson, UUID>, JpaSpecificationExecutor<ExternalPerson> {

    List<ExternalPerson> findByLastModifiedBefore(LocalDate lastModifiedBefore);

}
