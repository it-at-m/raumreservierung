package de.muenchen.raumreservierung.person;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalPersonRepository extends JpaRepository<ExternalPerson, UUID>, JpaSpecificationExecutor<ExternalPerson> {
}
