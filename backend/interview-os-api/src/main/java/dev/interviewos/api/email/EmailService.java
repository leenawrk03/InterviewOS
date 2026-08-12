package dev.interviewos.api.email;

import com.google.api.services.calendar.model.Event;

import dev.interviewos.api.config.MailProperties;
import dev.interviewos.api.user.AppUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Sends the user an email summarizing the Google Calendar events we just
 * fetched on their behalf, so the fetch and the notification always
 * happen together, in the same request.
 */
@Service
public class EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    /**
     * Fallback "from" address, read directly from the SMTP account itself
     * (spring.mail.username) rather than through a nested YAML default,
     * so there's exactly one place this can go wrong.
     */
    private final String smtpUsername;

    public EmailService(
            JavaMailSender mailSender,
            MailProperties mailProperties,
            @Value("${spring.mail.username}") String smtpUsername) {

        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.smtpUsername = smtpUsername;
    }

    /**
     * Emails {@code user} a summary of the events that were just fetched
     * from their Google Calendar. No-op if the fetch returned zero events.
     * Failures are logged, never thrown, so a broken mail server can
     * never break the calendar fetch itself.
     */
    public void sendUpcomingEventsEmail(AppUser user, List<Event> events) {

        if (!mailProperties.enabled()) {
            return;
        }

        if (events == null || events.isEmpty()) {
            return;
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn(
                    "Skipping calendar-fetch email: user {} has no email",
                    user.getId()
            );
            return;
        }

        try {
            String from = (mailProperties.from() != null
                    && !mailProperties.from().isBlank())
                    ? mailProperties.from().trim()
                    : smtpUsername.trim();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(user.getEmail().trim());
            message.setSubject(subjectFor(events));
            message.setText(bodyFor(events));

            mailSender.send(message);

            log.info(
                    "Sent calendar-fetch email to {} ({} event(s))",
                    user.getEmail(),
                    events.size()
            );

        } catch (MailException e) {
            // Logging the exception itself (not just getMessage()) so the
            // wrapped AddressException/root cause shows up in the logs.
            log.warn(
                    "Failed to send calendar-fetch email to {}",
                    user.getEmail(),
                    e
            );
        }
    }

    private String subjectFor(List<Event> events) {
        return "InterviewOS: " + events.size()
                + " upcoming event(s) from your calendar";
    }

    private String bodyFor(List<Event> events) {

        StringBuilder body = new StringBuilder(
                "We just fetched your Google Calendar. Here's what's "
                        + "coming up:\n\n"
        );

        for (Event event : events) {
            body.append("- ")
                    .append(event.getSummary() != null
                            ? event.getSummary()
                            : "(untitled event)")
                    .append("\n")
                    .append("  When: ")
                    .append(startTimeOf(event))
                    .append("\n");

            if (event.getHtmlLink() != null) {
                body.append("  Link: ")
                        .append(event.getHtmlLink())
                        .append("\n");
            }

            body.append("\n");
        }

        return body.toString();
    }

    private String startTimeOf(Event event) {

        if (event.getStart() == null) {
            return "unknown";
        }

        if (event.getStart().getDateTime() != null) {
            return event.getStart().getDateTime().toStringRfc3339();
        }

        if (event.getStart().getDate() != null) {
            return event.getStart().getDate().toString();
        }

        return "unknown";
    }
}
