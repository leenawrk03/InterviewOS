package dev.interviewos.api.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewAnalysisRepository
        extends JpaRepository<InterviewAnalysisEntity, UUID> {

    Optional<InterviewAnalysisEntity> findByUserIdAndEventId(UUID userId, String eventId);

    List<InterviewAnalysisEntity> findAllByUserIdAndEventIdIn(UUID userId, List<String> eventIds);
}
