package de.muenchen.raumreservierung.person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

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
    @Column(nullable = false)
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
