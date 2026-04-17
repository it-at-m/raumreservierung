package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.ExternalPerson;
import de.muenchen.raumreservierung.person.InternalPerson;
import de.muenchen.raumreservierung.person.Person;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface PersonMapper {

    @SubclassMapping(source = InternalPersonRequestDto.class, target = InternalPerson.class)
    @SubclassMapping(source = ExternalPersonRequestDto.class, target = ExternalPerson.class)
    Person toEntity(PersonRequestDto personRequestDto);

    @Named("toDto")
    @SubclassMapping(source = InternalPerson.class, target = InternalPersonResponseDto.class)
    @SubclassMapping(source = ExternalPerson.class, target = ExternalPersonResponseDto.class)
    PersonResponseDto toDto(Person person);

    default PersonResponseDto unproxyAndMapToDto(final Person person) {
        final Person unproxied = (Person) Hibernate.unproxy(person);
        return toDto(unproxied);
    }

    @Mapping(target = "type", constant = "INTERNAL")
    InternalPersonResponseDto toInternalDto(InternalPerson person);

    @Mapping(target = "type", constant = "EXTERNAL")
    ExternalPersonResponseDto toExternalDto(ExternalPerson person);
}
