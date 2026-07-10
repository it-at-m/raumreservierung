package de.muenchen.raumreservierung.person.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.muenchen.raumreservierung.person.domain.PersonTitle;
import de.muenchen.raumreservierung.person.domain.PersonType;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "INTERNAL", schema = InternalPersonRequestDto.class),
                @DiscriminatorMapping(value = "EXTERNAL", schema = ExternalPersonRequestDto.class)
        }
)
public interface PersonRequestDto {
    PersonTitle title();

    String firstName();

    String lastName();

    String telefonNumber();

    String email();

    PersonType type();
}
