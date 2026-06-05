package de.muenchen.raumreservierung.person.dto;

import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import de.muenchen.raumreservierung.person.domain.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface PersonMapper {

    String TYPE = "type";

    @SubclassMapping(source = InternalPersonRequestDto.class, target = InternalPerson.class)
    @SubclassMapping(source = ExternalPersonRequestDto.class, target = ExternalPerson.class)
    Person toEntity(PersonRequestDto personRequestDto);

    @SubclassMapping(source = InternalPerson.class, target = InternalPersonResponseDto.class)
    @SubclassMapping(source = ExternalPerson.class, target = ExternalPersonResponseDto.class)
    PersonResponseDto toDto(Person person);

    @Mapping(target = TYPE, constant = "INTERNAL")
    InternalPersonResponseDto toInternalDto(InternalPerson person);

    @Mapping(target = TYPE, constant = "EXTERNAL")
    ExternalPersonResponseDto toExternalDto(ExternalPerson person);

    @Named("useFilteredMapping")
    @SubclassMapping(source = InternalPerson.class, target = InternalPersonResponseDto.class, qualifiedByName = "toFilteredInternal")
    @SubclassMapping(source = ExternalPerson.class, target = ExternalPersonResponseDto.class, qualifiedByName = "toFilteredExternal")
    PersonResponseDto toFilteredDto(Person person);

    @Named("toFilteredInternal")
    @Mapping(target = TYPE, constant = "INTERNAL")
    @Mapping(target = "organisationId", ignore = true)
    @Mapping(target = "organisationUnit", ignore = true)
    @Mapping(target = "roleFunction", ignore = true)
    InternalPersonResponseDto toFilteredInternalDto(InternalPerson person);

    @Named("toFilteredExternal")
    @Mapping(target = TYPE, constant = "EXTERNAL")
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "streetAddress", ignore = true)
    @Mapping(target = "postalCodeCity", ignore = true)
    @Mapping(target = "note", ignore = true)
    ExternalPersonResponseDto toFilteredExternalDto(ExternalPerson person);
}
