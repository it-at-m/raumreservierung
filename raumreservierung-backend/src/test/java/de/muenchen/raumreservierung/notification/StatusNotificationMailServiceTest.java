package de.muenchen.raumreservierung.notification;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.oss.refarch.integration.email.application.port.out.MailOutPort;
import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.BookingStatus;
import de.muenchen.raumreservierung.configuration.MailInfoProperties;
import de.muenchen.raumreservierung.person.domain.ExternalPerson;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StatusNotificationMailServiceTest {

    @Mock
    private MailOutPort mailOutPort;

    @Mock
    private Configuration freemarkerConfig;

    @Mock
    private Template template;

    @Mock
    private MailInfoProperties mailInfoProperties;

    @InjectMocks
    private StatusNotificationMailService service;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(freemarkerConfig.getTemplate(anyString())).thenReturn(template);
        when(mailInfoProperties.getAppdomain()).thenReturn("testdomain.de");
        when(mailInfoProperties.getEnvironment()).thenReturn("unit-test");
    }

    @Test
    void testNoMailTypeForTransition() {
        InternalPerson person = new InternalPerson();
        person.setEmail("test@test.de");
        Booking booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setTitle("Test Booking");
        booking.setStatus(BookingStatus.ORGANIZER_CHANGED);
        booking.setBookedBy(person);

        service.sendStatusNotificationMail(booking, BookingStatus.COORDINATION_NEEDED);

        verify(mailOutPort, times(0)).sendHtmlMailWithTemplate(any());
    }

    @Test
    void testSendMail() throws Exception {
        InternalPerson person = new InternalPerson();
        person.setEmail("test@test.de");
        Booking booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setTitle("Test Booking");
        booking.setStatus(BookingStatus.ORGANIZER_APPROVED);
        booking.setBookedBy(person);

        service.sendStatusNotificationMail(booking, BookingStatus.COORDINATION_NEEDED);

        verify(mailOutPort, times(1)).sendHtmlMailWithTemplate(any());
    }

    @Test
    void testSkipSendWhenNoEmail() {
        InternalPerson person = new InternalPerson();
        person.setEmail(null);
        Booking booking = new Booking();
        booking.setBookedBy(person);
        booking.setId(UUID.randomUUID());
        booking.setTitle("Test Booking");
        booking.setStatus(BookingStatus.ORGANIZER_APPROVED);

        service.sendStatusNotificationMail(booking, BookingStatus.COORDINATION_NEEDED);

        verify(mailOutPort, never()).sendHtmlMailWithTemplate(any());
    }

    @Test
    void testSkipSendWhenReceiverIsExternal() {
        ExternalPerson person = new ExternalPerson();
        person.setEmail(null);
        Booking booking = new Booking();
        booking.setBookedBy(person);
        booking.setId(UUID.randomUUID());
        booking.setTitle("Test Booking");
        booking.setStatus(BookingStatus.ORGANIZER_APPROVED);

        service.sendStatusNotificationMail(booking, BookingStatus.COORDINATION_NEEDED);

        verify(mailOutPort, never()).sendHtmlMailWithTemplate(any());
    }
}
