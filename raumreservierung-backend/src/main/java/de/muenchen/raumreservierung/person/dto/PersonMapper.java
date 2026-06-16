package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.person.domain.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    @Mapping(target = "type", constant = "INTERNAL")
    InternalPersonResponseDto toInternalDto(InternalPerson person);

    @Mapping(target = "type", constant = "EXTERNAL")
    ExternalPersonResponseDto toExternalDto(ExternalPerson person);

    MinimalPersonResponseDto toMinimalDto(PersonResponseDto personResponseDto);
}
