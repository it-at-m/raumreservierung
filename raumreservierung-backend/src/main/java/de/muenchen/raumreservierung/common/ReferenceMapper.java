package de.muenchen.raumreservierung.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Component;

@Component
public class ReferenceMapper {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Reference mapper for all entities implementing BaseEntity with an UUID
     *
     * @param id UUID of the given entity
     * @param entityClass class which should be referenced
     * @return the reference of the entity of the given class
     */
    public <T extends BaseEntity> T resolve(UUID id, @TargetType Class<T> entityClass) {
        return id == null ? null : entityManager.getReference(entityClass, id);
    }

}
