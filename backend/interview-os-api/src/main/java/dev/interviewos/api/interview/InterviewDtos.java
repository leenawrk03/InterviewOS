package dev.interviewos.api.interview;

/** Matches the Angular Interview + DashboardStats models. */
public final class InterviewDtos {

    public record InterviewResponse(
        String id, String company, String role, String startsAt, String stage, boolean prepared) {
    }

    public record DashboardStats(long upcoming, long thisWeek, long prepared) {
    }

    private InterviewDtos() {
    }
}
