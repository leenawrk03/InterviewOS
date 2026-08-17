package dev.interviewos.api.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai")
public record AiProperties(
        String provider,
        boolean enabled,
        Gemini gemini
) {
    public record Gemini(
            String apiKey,
            String model,
            String baseUrl,
            int timeoutSeconds
    ) {}
}
