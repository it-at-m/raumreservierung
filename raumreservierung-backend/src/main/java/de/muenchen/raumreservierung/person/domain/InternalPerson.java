package de.muenchen.raumreservierung.person.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class InternalPerson extends Person {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true)
    private String organisationId;

    @Column(nullable = false)
    private String organisationUnit;

    /**
     * Role in this application
     */
    @Column()
    private String roleFunction;

    @Override
    public void updateFrom(final Person person) {
        if (!(person instanceof InternalPerson internalPerson)) {
            throw new IllegalArgumentException();
        }
        super.updateBaseFields(internalPerson);
        this.organisationId = internalPerson.getOrganisationId();
        this.organisationUnit = internalPerson.getOrganisationUnit();
        this.roleFunction = internalPerson.getRoleFunction();
    }

}
