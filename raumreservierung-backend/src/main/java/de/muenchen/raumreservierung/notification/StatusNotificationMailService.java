package de.muenchen.raumreservierung.notification;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.muenchen.oss.refarch.integration.email.application.port.out.MailOutPort;
import de.muenchen.oss.refarch.integration.email.domain.model.TemplateMail;
import de.muenchen.raumreservierung.booking.Booking;
import de.muenchen.raumreservierung.booking.BookingStatus;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
@Slf4j
public class StatusNotificationMailService {

    private final MailOutPort mailOutPort;
    Configuration freemarkerConfig;

    public void sendStatusNotificationMail(final String receivers, final Booking booking, final BookingStatus oldStatus) {
        /* TODO: Determine the mail to send depending on the booking and the old status
                 In this way this method can be called everytime a status update is triggered and then we decide here
                 if and which mail to send.
                 -
                 Check whether the receivers should be expected as a param or if its more suitable to determine them
                 inside this method.
        */

        MailType mailType = StatusNotificationTransitionMap.getMailTypeForTransition(oldStatus, booking.getStatus());

        if (mailType == null) {
            log.debug("No mail to send for transition from {} to {}", oldStatus, booking.getStatus());
            return;
        }

        log.info("Send status notification '{}' email", mailType.name());

        Map<String, Object> content = new HashMap<>();
        content.put("booking", booking);

        /* TODO: Check how to handle subject templating, based on the subjects the Fachbereich provides.
                 If no templating needs to be performed we can simply use the enum value.
                 If templating is needed we need to check which data needs to be provided. Maybe map based access would
                 be suitable so we provide all the possibly needed data via a map and during templating the required
                 data is accessed and used.
        */
        String subject = mailType.getSubjectTemplate();
        try {
            Template t = new Template("subjectLine", new StringReader(subject), freemarkerConfig);
            StringWriter writer = new StringWriter();
            t.process(content, writer);
            subject = writer.toString();
        } catch (Exception e) {
            log.error("Failed to render subject template for {}", mailType.name(), e);
            subject = mailType.getSubjectTemplate();
        }


        String templateString = mailType.getTemplateFileName();
        TemplateMail template = new TemplateMail(receivers, "", "", subject, "", List.of(), templateString, content);
        mailOutPort.sendHtmlMailWithTemplate(template);
    }
}