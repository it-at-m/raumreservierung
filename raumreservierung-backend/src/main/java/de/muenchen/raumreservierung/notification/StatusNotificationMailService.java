package de.muenchen.raumreservierung.notification;

import de.muenchen.oss.refarch.integration.email.application.port.out.MailOutPort;
import de.muenchen.oss.refarch.integration.email.domain.model.TemplateMail;
import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.BookingStatus;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
@SuppressFBWarnings(
        value = "TEMPLATE_INJECTION_FREEMARKER",
        justification = "Templates werden ausschließlich aus einer sicheren Quelle geladen (Hardcoded in Enum)."
)
public class StatusNotificationMailService {

    private final MailOutPort mailOutPort;
    private final Configuration freemarkerConfig;

    @Value("${mail.environment}")
    private String environment;

    @Value("${mail.domain}")
    private String domain;

    public void sendStatusNotificationMail(final Booking booking, final BookingStatus oldStatus) {
        final MailType mailType = StatusNotificationTransitionMap.getMailTypeForTransition(oldStatus, booking.getStatus());

        if (mailType == null) {
            log.debug("No mail to send for transition from {} to {}", oldStatus, booking.getStatus());
            return;
        }

        log.info("Send status notification '{}' email", mailType.name());

        final Map<String, Object> content = new HashMap<>();
        content.put("booking", booking);
        content.put("environment", environment);
        content.put("domain", domain);

        String subject;
        String receivers = booking.getBookedBy() instanceof InternalPerson ? booking.getBookedBy().getEmail() : null;

        if (receivers == null) {
            log.error("BookedBy contains no email address. Skip sending notification mail for booking id: {}; name: {}", booking.getId(), booking.getTitle());
            return;
        }

        try {
            final Template t = freemarkerConfig.getTemplate(mailType.getSubjectTemplate());
            final StringWriter writer = new StringWriter();
            t.process(content, writer);
            subject = writer.toString();
        } catch (Exception e) {
            log.error("Failed to render subject template for {}", mailType.name(), e);
            subject = "Es gibt Neuigkeiten zu ihrer Buchung";
        }

        final String templateString = mailType.getTemplateFileName();
        final TemplateMail template = new TemplateMail(receivers, "", "", subject, "", List.of(), templateString, content);
        mailOutPort.sendHtmlMailWithTemplate(template);
    }
}
