package de.muenchen.raumreservierung.person.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.muenchen.raumreservierung.person.PersonType;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes(
    {
            @JsonSubTypes.Type(value = InternalPersonResponseDto.class, name = "INTERNAL"),
            @JsonSubTypes.Type(value = ExternalPersonResponseDto.class, name = "EXTERNAL")
    }
)
public interface PersonResponseDto {
    UUID id();

    String name();

    String telefonNumber();

    String email();

    PersonType type();

}
