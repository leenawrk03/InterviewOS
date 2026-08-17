package dev.interviewos.api.ai;

import dev.interviewos.api.user.AppUser;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "interview_analysis",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}),
        indexes = @Index(name = "idx_analysis_user_event", columnList = "user_id,event_id")
)
public class InterviewAnalysisEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    /** Hash of the calendar details the analysis was built from — drives invalidation. */
    @Column(name = "source_hash", nullable = false, length = 64)
    private String sourceHash;

    @Column(nullable = false)
    private String provider;

    /** Full structured analysis as JSON. */
    @Column(name = "analysis_json", nullable = false, columnDefinition = "TEXT")
    private String analysisJson;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public UUID getId() { return id; }
    public AppUser getUser() { return user; }
    public String getEventId() { return eventId; }
    public String getSourceHash() { return sourceHash; }
    public String getProvider() { return provider; }
    public String getAnalysisJson() { return analysisJson; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setUser(AppUser user) { this.user = user; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public void setSourceHash(String sourceHash) { this.sourceHash = sourceHash; }
    public void setProvider(String provider) { this.provider = provider; }
    public void setAnalysisJson(String analysisJson) { this.analysisJson = analysisJson; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
