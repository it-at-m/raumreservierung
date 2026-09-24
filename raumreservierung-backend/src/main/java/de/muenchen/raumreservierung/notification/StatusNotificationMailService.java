package de.muenchen.raumreservierung.notification;

import de.muenchen.oss.refarch.integration.email.application.port.out.MailOutPort;
import de.muenchen.oss.refarch.integration.email.domain.model.TemplateMail;
import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.BookingStatus;
import de.muenchen.raumreservierung.booking.BookingStatusAnwenderFrontend;
import de.muenchen.raumreservierung.configuration.MailInfoConfiguration;
import de.muenchen.raumreservierung.person.domain.InternalPerson;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.StringWriter;
import java.util.HashMap;
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

    private final static String DEFAULT_SUBJECT = "Es gibt Neuigkeiten zu ihrer Buchung";
    private final static String GENERIC_MAIL_TEMPLATE_NAME = "%s-mail-template.ftl";
    private final static String GENERIC_SUBJECT_TEMPLATE_NAME = "%s-subject-template.ftl";

    private final MailOutPort mailOutPort;
    private final Configuration freemarkerConfig;
    private final MailInfoConfiguration mailInfoConfiguration;

    @Async("mailExecutor")
    public void sendStatusNotificationMail(final Booking booking, final BookingStatus oldStatus) {
        BookingStatusAnwenderFrontend newStatus = booking.getStatus().getFrontendAnwenderStatus();
        if (newStatus == oldStatus.getFrontendAnwenderStatus()) {
            log.debug("No status change for booking {} - Skip sending mail", booking.getId());
            return;
        }

        log.info("Send status notification '{}' email", newStatus.name());

        final Map<String, Object> content = new HashMap<>();
        content.put("booking", booking);
        content.put("environment", mailInfoConfiguration.getEnvironment());
        content.put("domain", mailInfoConfiguration.getDomain());

        final String receivers = booking.getBookedBy() instanceof InternalPerson ? booking.getBookedBy().getEmail() : null;

        if (receivers == null) {
            log.error("BookedBy contains no email address. Skip sending notification mail for booking id: {}; name: {}", booking.getId(), booking.getTitle());
            return;
        }

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

        final String templateString = String.format(GENERIC_MAIL_TEMPLATE_NAME, newStatus.getMailTemplateName());
        final TemplateMail template = new TemplateMail(receivers, "", "", subject, "", List.of(), templateString, content);
        mailOutPort.sendHtmlMailWithTemplate(template);
    }
}
