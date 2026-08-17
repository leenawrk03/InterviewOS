package dev.interviewos.api.ai;

import java.time.Instant;

public record InterviewPrepResponse(
        String eventId,
        InterviewAnalysis analysis,
        String provider,
        Instant generatedAt,
        boolean fromCache
) {}
