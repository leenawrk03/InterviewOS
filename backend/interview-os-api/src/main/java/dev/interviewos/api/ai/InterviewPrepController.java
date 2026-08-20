package dev.interviewos.api.ai;

import dev.interviewos.api.user.AppUser;
import dev.interviewos.api.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interviews")
public class InterviewPrepController {

    private final InterviewPrepService interviewPrepService;
    private final UserService userService;

    public InterviewPrepController(
            InterviewPrepService interviewPrepService,
            UserService userService) {
        this.interviewPrepService = interviewPrepService;
        this.userService = userService;
    }

    /** Health check target for Render; must accept GET with no auth. */
    @GetMapping("/prep/ping")
    public ResponseEntity<Void> ping() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/prep/{eventId}")
    public ResponseEntity<InterviewPrepResponse> generatePrep(
            @PathVariable String eventId,
            @RequestParam(defaultValue = "false") boolean force,
            @AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        String googleId =
                (String) principal.getAttributes().get("sub");

        AppUser user = userService.findByGoogleId(googleId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        InterviewPrepResponse response =
                interviewPrepService.getOrCreate(
                        user,
                        eventId,
                        force
                );

        return ResponseEntity.ok(response);
    }
}