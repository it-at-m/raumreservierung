package de.muenchen.raumreservierung.security;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muenchen.raumreservierung.booking.WithMockJwt;
import de.muenchen.raumreservierung.configuration.security.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        classes = {
                //                BookingService.class,
                SecurityConfiguration.class,
                SecurityContextService.class
        }
)
public class SecurityContextServiceIntegrationTest {
    @Autowired
    private SecurityContextService securityContextService;
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @WithMockJwt(lhmObjectID = "000005", authorities = { Roles.RAUM_ADMIN })
    void hasAuthority_ShouldReturnTrue_WhenUserHasAdminRole() {
        boolean has = securityContextService.hasAuthority(Roles.RAUM_BUCHUNG);
        assertTrue(has);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000005", authorities = {})
    void hasAuthority_ReturnsFalse_WhenUserHasNoAuthoritiesAtAll() {
        boolean has = securityContextService.hasAuthority(Roles.ANWENDER);
        assertFalse(has);
    }

    @Test
    void hasAuthority_ReturnsFalse_WhenContextIsEmpty() {
        boolean has = securityContextService.hasAuthority(Roles.ANWENDER);
        assertFalse(has);
    }

    @Test
    @WithMockJwt(lhmObjectID = "000001", authorities = {})
    void getCurrentOID_withAuthenticatedJwtUser_returnsConfiguredOid() {
        String currentOid = securityContextService.getCurrentOID();

        assertThat(currentOid).isEqualTo("000001");
    }

}
