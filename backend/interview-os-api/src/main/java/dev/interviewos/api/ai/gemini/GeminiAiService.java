package dev.interviewos.api.ai.gemini;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.interviewos.api.ai.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "gemini", matchIfMissing = true)
public class GeminiAiService implements AiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiService.class);

    private final AiProperties props;
    private final ObjectMapper mapper;
    private final RestClient http;

    public GeminiAiService(AiProperties props, ObjectMapper mapper) {
        this.props = props;
        this.mapper = mapper;

        var factory = new SimpleClientHttpRequestFactory();
        var timeout = Duration.ofSeconds(props.gemini().timeoutSeconds());
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);

        this.http = RestClient.builder()
                .baseUrl(props.gemini().baseUrl())
                .requestFactory(factory)
                .build();
    }

    @Override
    public String providerName() {
        return "gemini:" + props.gemini().model();
    }

    @Override
    public InterviewAnalysis analyseInterview(InterviewPrepRequest request) {

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of(
                        "parts", new Object[]{ Map.of("text", PromptTemplates.SYSTEM_PROMPT) }
                ),
                "contents", new Object[]{
                        Map.of(
                                "role", "user",
                                "parts", new Object[]{
                                        Map.of("text", PromptTemplates.userPrompt(request))
                                }
                        )
                },
                "generationConfig", Map.of(
                        "temperature", 0.4,
                        "maxOutputTokens", 4096,
                        "responseMimeType", "application/json"
                )
        );

        String raw;
        try {
            raw = http.post()
                    .uri("/models/{model}:generateContent?key={key}",
                            props.gemini().model(), props.gemini().apiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            log.error("Gemini call failed for event {}", request.eventId(), e);
            throw new AiGenerationException("AI provider request failed: " + e.getMessage(), e);
        }

        return parse(raw, request.eventId());
    }

    private InterviewAnalysis parse(String raw, String eventId) {
        try {
            JsonNode root = mapper.readTree(raw);
            JsonNode text = root.path("candidates").path(0)
                    .path("content").path("parts").path(0).path("text");

            if (text.isMissingNode() || text.asText().isBlank()) {
                throw new AiGenerationException(
                        "AI provider returned no content for event " + eventId);
            }
            return mapper.readValue(stripFences(text.asText()), InterviewAnalysis.class);

        } catch (AiGenerationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Could not parse Gemini response for event {}: {}", eventId, raw, e);
            throw new AiGenerationException("AI response was not valid JSON", e);
        }
    }

    /** Defensive: some models still wrap JSON in ```json fences. */
    private static String stripFences(String s) {
        String t = s.trim();
        if (t.startsWith("```")) {
            t = t.replaceFirst("^```[a-zA-Z]*\\s*", "").replaceFirst("```\\s*$", "");
        }
        return t.trim();
    }
}
