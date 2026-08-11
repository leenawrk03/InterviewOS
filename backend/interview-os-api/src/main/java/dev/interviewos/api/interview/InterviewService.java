package dev.interviewos.api.interview;

import dev.interviewos.api.interview.InterviewDtos.DashboardStats;
import dev.interviewos.api.interview.InterviewDtos.InterviewResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InterviewService {

    private final InterviewRepository repository;

    public InterviewService(InterviewRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> upcoming(UUID userId) {
        return repository.findByUserIdAndStartsAtAfterOrderByStartsAtAsc(userId, Instant.now())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public DashboardStats stats(UUID userId) {
        Instant now = Instant.now();
        List<Interview> upcoming =
            repository.findByUserIdAndStartsAtAfterOrderByStartsAtAsc(userId, now);
        long thisWeek = repository
            .findByUserIdAndStartsAtBetween(userId, now, now.plus(7, ChronoUnit.DAYS))
            .size();
        long prepared = upcoming.stream().filter(Interview::isPrepared).count();
        return new DashboardStats(upcoming.size(), thisWeek, prepared);
    }

    private InterviewResponse toResponse(Interview interview) {
        return new InterviewResponse(
            interview.getId().toString(),
            interview.getCompany(),
            interview.getRole(),
            interview.getStartsAt().toString(),
            interview.getStage(),
            interview.isPrepared());
    }
}
