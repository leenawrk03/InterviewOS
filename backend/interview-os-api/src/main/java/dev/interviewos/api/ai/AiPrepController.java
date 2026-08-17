package dev.interviewos.api.ai;

import dev.interviewos.api.user.AppUser;
import dev.interviewos.api.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interviews")
public class AiPrepController {

    private final InterviewPrepService prepService;
    private final UserRepository userRepository;

    public AiPrepController(InterviewPrepService prepService, UserRepository userRepository) {
        this.prepService = prepService;
        this.userRepository = userRepository;
    }

    /** Generate (or return the stored) AI preparation for one calendar event. */
    @PostMapping("/{eventId}/prep")
    public ResponseEntity<?> generate(@PathVariable String eventId,
                                      @RequestParam(defaultValue = "false") boolean force,
                                      Principal principal) {
        try {
            return ResponseEntity.ok(prepService.getOrCreate(currentUser(principal), eventId, force));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (AiGenerationException e) {
            return ResponseEntity.status(502).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /** Stored analyses only — cheap call the dashboard makes on load. */
    @GetMapping("/prep")
    public ResponseEntity<?> cached(@RequestParam List<String> eventIds, Principal principal) {
        try {
            return ResponseEntity.ok(prepService.findCached(currentUser(principal), eventIds));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    private AppUser currentUser(Principal principal) {
        return userRepository.findByGoogleId(principal.getName())
                .orElseThrow(() -> new IllegalStateException("InterviewOS user not found"));
    }
}
