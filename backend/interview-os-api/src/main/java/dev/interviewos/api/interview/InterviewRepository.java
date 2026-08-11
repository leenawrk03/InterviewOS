package dev.interviewos.api.interview;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, UUID> {

    List<Interview> findByUserIdAndStartsAtAfterOrderByStartsAtAsc(UUID userId, Instant from);

    List<Interview> findByUserIdAndStartsAtBetween(UUID userId, Instant from, Instant to);
}
