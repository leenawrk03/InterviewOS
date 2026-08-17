package dev.interviewos.api.ai;

/** Normalised calendar details handed to the AI provider. */
public record InterviewPrepRequest(
        String eventId,
        String title,
        String description,
        String startTime,
        String endTime,
        String link
) {}
