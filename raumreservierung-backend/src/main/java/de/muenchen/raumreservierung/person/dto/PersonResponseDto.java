package de.muenchen.raumreservierung.person.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.muenchen.raumreservierung.person.domain.PersonTitle;
import de.muenchen.raumreservierung.person.domain.PersonType;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
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
        name = "PersonResponseDto",
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "INTERNAL", schema = InternalPersonResponseDto.class),
                @DiscriminatorMapping(value = "EXTERNAL", schema = ExternalPersonResponseDto.class)
        }
)
@SuppressWarnings("PMD.ShortMethodName")
public interface PersonResponseDto {
    UUID id();

    PersonTitle title();

    String firstName();

    String lastName();

    String telefonNumber();

    String email();

    PersonType type();

}
