package de.muenchen.raumreservierung.holidays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.raumreservierung.holidays.dto.HolidayMapper;
import de.muenchen.raumreservierung.holidays.dto.HolidayRequestDTO;
import de.muenchen.raumreservierung.holidays.dto.HolidayResponseDTO;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@AllArgsConstructor
public class HolidayMapperTest {
    private final HolidayMapper holidayMapper = Mappers.getMapper(HolidayMapper.class);

    @Nested
    class ToDTO {
        @Test
        void givenHoliday_thenReturnsCorrectDTO() {
            // Given
            final UUID uuid = UUID.randomUUID();
            final Holiday holiday = new Holiday();
            holiday.setId(uuid);
            holiday.setName("Weihnachten");
            holiday.setStartDate(LocalDate.of(2026, 12, 24));
            holiday.setEndDate(LocalDate.of(2026, 12, 24));

            final HolidayResponseDTO holidayResponseDTO = new HolidayResponseDTO("Weihnachten", LocalDate.of(2026, 12, 24), LocalDate.of(2026, 12, 24), uuid);

            // When
            final HolidayResponseDTO result = holidayMapper.toDTO(holiday);

            // Then
            assertNotNull(result);
            assertThat(result).usingRecursiveComparison().isEqualTo(holidayResponseDTO);
        }
    }

    @Nested
    class ToEntity {
        @Test
        void givenRequestDTO_thenReturnsCorrectEntity() {
            // Given
            final UUID uuid = UUID.randomUUID();
            final HolidayRequestDTO requestDTO = new HolidayRequestDTO("Weihnachten", LocalDate.of(2026, 12, 24),
                    LocalDate.of(2026, 12, 24));

            final Holiday holiday = new Holiday();
            holiday.setId(uuid);
            holiday.setName("Weihnachten");
            holiday.setStartDate(LocalDate.of(2026, 12, 24));
            holiday.setEndDate(LocalDate.of(2026, 12, 24));

            // When
            final Holiday result = holidayMapper.toEntity(requestDTO);

            // Then
            assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(holiday);
        }
    }
}
