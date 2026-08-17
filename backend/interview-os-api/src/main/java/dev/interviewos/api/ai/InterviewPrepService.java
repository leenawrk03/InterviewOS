package dev.interviewos.api.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.interviewos.api.calendar.CalendarInterviewResponse;
import dev.interviewos.api.calendar.GoogleCalendarService;
import dev.interviewos.api.user.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Service
public class InterviewPrepService {

    private final AiService aiService;
    private final InterviewAnalysisRepository repository;
    private final GoogleCalendarService calendarService;
    private final ObjectMapper mapper;

    public InterviewPrepService(AiService aiService,
                                InterviewAnalysisRepository repository,
                                GoogleCalendarService calendarService,
                                ObjectMapper mapper) {
        this.aiService = aiService;
        this.repository = repository;
        this.calendarService = calendarService;
        this.mapper = mapper;
    }

    /** Cache-first. Regenerates only when the calendar details changed, or force=true. */
    @Transactional
    public InterviewPrepResponse getOrCreate(AppUser user, String eventId, boolean force) {

        CalendarInterviewResponse event = findEvent(user, eventId);
        InterviewPrepRequest request = new InterviewPrepRequest(
                event.id(), event.title(), event.description(),
                event.startTime(), event.endTime(), event.link());

        String hash = sourceHash(request);

        Optional<InterviewAnalysisEntity> cached =
                repository.findByUserIdAndEventId(user.getId(), eventId);

        if (!force && cached.isPresent() && cached.get().getSourceHash().equals(hash)) {
            return toResponse(cached.get(), true);
        }

        InterviewAnalysis analysis = aiService.analyseInterview(request);

        InterviewAnalysisEntity entity = cached.orElseGet(InterviewAnalysisEntity::new);
        entity.setUser(user);
        entity.setEventId(eventId);
        entity.setSourceHash(hash);
        entity.setProvider(aiService.providerName());
        entity.setAnalysisJson(writeJson(analysis));
        entity.setUpdatedAt(Instant.now());

        return toResponse(repository.save(entity), false);
    }

    /** Only what is already stored — never calls the AI provider. */
    @Transactional(readOnly = true)
    public List<InterviewPrepResponse> findCached(AppUser user, List<String> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) return List.of();
        return repository.findAllByUserIdAndEventIdIn(user.getId(), eventIds)
                .stream()
                .map(e -> toResponse(e, true))
                .toList();
    }

    private CalendarInterviewResponse findEvent(AppUser user, String eventId) {
        try {
            return calendarService.getUpcomingInterviewEvents(user).stream()
                    .filter(e -> eventId.equals(e.id()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Calendar event not found: " + eventId));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Could not read calendar event", e);
        }
    }

    private InterviewPrepResponse toResponse(InterviewAnalysisEntity e, boolean fromCache) {
        try {
            return new InterviewPrepResponse(
                    e.getEventId(),
                    mapper.readValue(e.getAnalysisJson(), InterviewAnalysis.class),
                    e.getProvider(),
                    e.getUpdatedAt(),
                    fromCache);
        } catch (Exception ex) {
            throw new AiGenerationException("Stored analysis is corrupt", ex);
        }
    }

    private String writeJson(InterviewAnalysis analysis) {
        try {
            return mapper.writeValueAsString(analysis);
        } catch (Exception e) {
            throw new AiGenerationException("Could not serialise analysis", e);
        }
    }

    private static String sourceHash(InterviewPrepRequest r) {
        String raw = String.join("|",
                nz(r.title()), nz(r.description()), nz(r.startTime()), nz(r.endTime()));
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
