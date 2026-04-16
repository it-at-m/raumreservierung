package de.muenchen.raumreservierung.person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ExternalPerson extends Person {

    @Serial
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column
    private PersonTitle title;

    @Column
    private String company;

    @Column
    private String streetAddress;

    @Column
    private String postalCodeCity;

    @Column
    private String note;

    @Override
    public void updateFrom(final Person person) {
        if (!(person instanceof ExternalPerson externalPerson)) {
            throw new IllegalArgumentException();
        }
        super.updateBaseFields(externalPerson);
        this.title = externalPerson.getTitle();
        this.company = externalPerson.getCompany();
        this.streetAddress = externalPerson.getStreetAddress();
        this.postalCodeCity = externalPerson.getPostalCodeCity();
        this.note = externalPerson.getNote();
    }
}
