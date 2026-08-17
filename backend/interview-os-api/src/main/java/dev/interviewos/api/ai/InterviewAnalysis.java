package dev.interviewos.api.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** Structured output returned by any AiService implementation. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InterviewAnalysis(
        String summary,
        List<Topic> topics,
        List<Question> questions,
        List<PrepStep> preparationPlan,
        List<Resource> resources,
        List<String> lastMinuteTips
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Topic(String name, String priority, String why) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Question(String question, String category, String answerHint) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PrepStep(int order, String title, String detail, Integer estimatedMinutes) {}

    /** urlVerified stays false until the resource-search phase lands. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Resource(String title, String url, String type, String why, boolean urlVerified) {}
}
