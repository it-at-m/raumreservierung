package de.muenchen.raumreservierung.person;

import de.muenchen.raumreservierung.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String telefonNumber;

    @Column(nullable = false)
    private String email;

    public abstract void updateFrom(Person person);

    public void updateBaseFields(final Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.telefonNumber = person.getTelefonNumber();
        this.email = person.getEmail();
    }
}
