package dev.interviewos.api.calendar;

public record CalendarInterviewResponse(
        String id,
        String title,
        String description,
        String startTime,
        String endTime,
        String link
) {
}