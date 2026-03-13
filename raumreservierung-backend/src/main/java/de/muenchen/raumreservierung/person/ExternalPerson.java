package de.muenchen.raumreservierung.person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Entity
@Getter
@Setter
public class ExternalPerson extends Person {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column
    private String company;

    @Column
    private String streetAddress;

    @Column
    private String postalCodeCity;

    @Override
    public void updateFrom(final Person person) {
        if (!(person instanceof ExternalPerson externalPerson)) {
            throw new IllegalArgumentException();
        }
        super.updateBaseFields(externalPerson);
        this.company = externalPerson.getCompany();
        this.streetAddress = externalPerson.getStreetAddress();
        this.postalCodeCity = externalPerson.getPostalCodeCity();
    }
}
