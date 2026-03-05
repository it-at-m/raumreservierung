package de.muenchen.raumreservierung.person.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.muenchen.raumreservierung.person.PersonType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        include = JsonTypeInfo.As.EXISTING_PROPERTY
)
@JsonSubTypes(
    {
            @JsonSubTypes.Type(value = InternalPersonRequestDto.class, name = "INTERNAL"),
            @JsonSubTypes.Type(value = ExternalPersonRequestDto.class, name = "EXTERNAL")
    }
)
public interface PersonRequestDto {
    String name();

    String telefonNumber();

    String email();

    PersonType type();
}
