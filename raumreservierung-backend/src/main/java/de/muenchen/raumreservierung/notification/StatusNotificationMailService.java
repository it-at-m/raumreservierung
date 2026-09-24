package de.muenchen.raumreservierung.notification;

import de.muenchen.oss.refarch.integration.email.application.port.out.MailOutPort;
import de.muenchen.oss.refarch.integration.email.domain.model.TemplateMail;
import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.BookingStatus;
import de.muenchen.raumreservierung.booking.BookingStatusAnwenderFrontend;
import de.muenchen.raumreservierung.configuration.MailInfoProperties;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
@SuppressFBWarnings(
        value = "TEMPLATE_INJECTION_FREEMARKER",
        justification = "Templates are loaded from classpath"
)
public class StatusNotificationMailService {

    private static final String DEFAULT_SUBJECT = "Es gibt Neuigkeiten zu ihrer Buchung";
    private static final String GENERIC_MAIL_TEMPLATE_NAME = "%s-mail-template.ftl";
    private static final String GENERIC_SUBJECT_TEMPLATE_NAME = "%s-subject-template.ftl";

    private final MailOutPort mailOutPort;
    private final Configuration freemarkerConfig;
    private final MailInfoProperties mailInfoProperties;

    @Async("mailExecutor")
    public void sendStatusNotificationMail(final Booking booking, final BookingStatus oldStatus) {
        final BookingStatusAnwenderFrontend newStatus = booking.getStatus().getFrontendAnwenderStatus();
        if (newStatus == oldStatus.getFrontendAnwenderStatus()) {
            log.debug("No status change for booking {} - Skip sending mail", booking.getId());
            return;
        }

        if (!(booking.getBookedBy() instanceof InternalPerson internalPerson) || internalPerson.getEmail() == null) {
            log.error("BookedBy contains no email address. Skip sending notification mail for booking id: {}; name: {}", booking.getId(), booking.getTitle());
            return;
        }

        log.info("Send status notification '{}' email", newStatus.name());

        final Map<String, Object> content = Map.of(
                "booking", booking,
                "environment", mailInfoProperties.getEnvironment(),
                "domain", mailInfoProperties.getAppdomain());

        final String subject = renderSubject(newStatus, content);

        final String templateString = String.format(GENERIC_MAIL_TEMPLATE_NAME, newStatus.getMailTemplateName());
        final TemplateMail template = new TemplateMail(internalPerson.getEmail(), "", "", subject, "", List.of(), templateString, content);
        mailOutPort.sendHtmlMailWithTemplate(template);
    }

    private String renderSubject(final BookingStatusAnwenderFrontend newStatus, final Map<String, Object> content) {
        String subject;
        try {
            final Template t = freemarkerConfig.getTemplate(String.format(GENERIC_SUBJECT_TEMPLATE_NAME, newStatus.getMailTemplateName()));
            final StringWriter writer = new StringWriter();
            t.process(content, writer);
            subject = writer.toString();
        } catch (Exception e) {
            log.error("Failed to render subject template for {}", newStatus.name(), e);
            subject = DEFAULT_SUBJECT;
        }
        return subject;
    }
}
