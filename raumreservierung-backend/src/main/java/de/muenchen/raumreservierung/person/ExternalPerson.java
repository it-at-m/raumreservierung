package de.muenchen.raumreservierung.person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ExternalPerson extends Person {

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
