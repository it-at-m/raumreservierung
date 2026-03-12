package de.muenchen.raumreservierung.person.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.muenchen.raumreservierung.person.PersonType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = InternalPersonResponseDto.class, name = "INTERNAL"),
                @JsonSubTypes.Type(value = ExternalPersonResponseDto.class, name = "EXTERNAL")
        }
)
@Schema(
        discriminatorProperty = "type",
        anyOf = {InternalPersonResponseDto.class, ExternalPersonResponseDto.class}
)
public interface PersonResponseDto {
    UUID id();

    String name();

    String telefonNumber();

    String email();

    PersonType type();

}
