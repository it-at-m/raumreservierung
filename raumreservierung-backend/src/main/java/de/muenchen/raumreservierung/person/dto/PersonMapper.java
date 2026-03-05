package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.ExternalPerson;
import de.muenchen.raumreservierung.person.InternalPerson;
import de.muenchen.raumreservierung.person.Person;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface PersonMapper {

    @SubclassMapping(source = InternalPersonRequestDto.class, target = InternalPerson.class)
    @SubclassMapping(source = ExternalPersonRequestDto.class, target = ExternalPerson.class)
    Person toEntity(PersonRequestDto personRequestDto);

    @SubclassMapping(source = InternalPerson.class, target = InternalPersonResponseDto.class)
    @SubclassMapping(source = ExternalPerson.class, target = ExternalPersonResponseDto.class)
    PersonResponseDto toDto(Person person);
}
