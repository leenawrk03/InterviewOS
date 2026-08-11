package dev.interviewos.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Bound from app.frontend.* in application.yml. */
@ConfigurationProperties(prefix = "app.frontend")
public record FrontendProperties(String baseUrl, String defaultRedirectPath) {
}
