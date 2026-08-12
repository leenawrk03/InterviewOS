package dev.interviewos.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Bound from app.mail.* in application.yml. */
@ConfigurationProperties(prefix = "app.mail")
public record MailProperties(String from, boolean enabled) {
}
