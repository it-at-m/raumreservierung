package de.muenchen.raumreservierung.person.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDate;

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

    @Column
    private String note;

    @Column(nullable = false)
    private LocalDate lastModified;

    @Override
    public void updateFrom(final Person person) {
        if (!(person instanceof ExternalPerson externalPerson)) {
            throw new IllegalArgumentException();
        }
        super.updateBaseFields(externalPerson);
        this.company = externalPerson.getCompany();
        this.streetAddress = externalPerson.getStreetAddress();
        this.postalCodeCity = externalPerson.getPostalCodeCity();
        this.note = externalPerson.getNote();
    }

    public void updateLastModified(final LocalDate futureDate) {
        final LocalDate today = LocalDate.now();

        this.lastModified = (futureDate != null && futureDate.isAfter(today)) ? futureDate : today;
    }

}
