package de.muenchen.raumreservierung.configuration.filter;

import static de.muenchen.raumreservierung.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.raumreservierung.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.raumreservierung.MicroServiceApplication;
import de.muenchen.raumreservierung.TestConstants;
import de.muenchen.raumreservierung.equipment.Equipment;
import de.muenchen.raumreservierung.equipment.EquipmentRepository;
import de.muenchen.raumreservierung.equipment.dto.EquipmentRequestDto;
import de.muenchen.raumreservierung.equipment.dto.EquipmentResponseDto;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class UnicodeFilterConfigurationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String ENTITY_ENDPOINT_URL = "/equipment";

    /**
     * Decomposed string:
     * String "Ä-é" represented with unicode letters "A◌̈-e◌́"
     */
    private static final String TEXT_ATTRIBUTE_DECOMPOSED = "\u0041\u0308-\u0065\u0301";

    /**
     * Composed string:
     * String "Ä-é" represented with unicode letters "Ä-é".
     */
    private static final String TEXT_ATTRIBUTE_COMPOSED = "\u00c4-\u00e9";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    void testForNfcNormalization() {
        // Given
        // Persist entity with decomposed string.
        final EquipmentRequestDto equipmentRequestDto = new EquipmentRequestDto(TEXT_ATTRIBUTE_DECOMPOSED, TEXT_ATTRIBUTE_DECOMPOSED, true);

        // When
        final EquipmentResponseDto response = testRestTemplate.postForEntity(URI.create(ENTITY_ENDPOINT_URL), equipmentRequestDto, EquipmentResponseDto.class)
                .getBody();
        assertNotNull(response);
        final Equipment equipment = equipmentRepository.findById(response.id()).orElse(null);

        // Then
        // Check whether response contains a composed string.
        assertNotNull(response.name());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, response.name());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), response.name().length());

        // Check persisted entity contains a composed string via JPA repository.
        assertNotNull(equipment);
        assertNotNull(equipment.getName());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, equipment.getName());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), equipment.getName().length());
    }

}
