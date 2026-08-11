package dev.interviewos.api.interview;

import dev.interviewos.api.interview.InterviewDtos.DashboardStats;
import dev.interviewos.api.interview.InterviewDtos.InterviewResponse;
import dev.interviewos.api.user.AppUser;
import dev.interviewos.api.user.UserService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviews;
    private final UserService users;

    public InterviewController(InterviewService interviews, UserService users) {
        this.interviews = interviews;
        this.users = users;
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<InterviewResponse>> upcoming(
        @AuthenticationPrincipal OAuth2User principal) {
        return ResponseEntity.ok(interviews.upcoming(currentUser(principal).getId()));
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> stats(@AuthenticationPrincipal OAuth2User principal) {
        return ResponseEntity.ok(interviews.stats(currentUser(principal).getId()));
    }

    private AppUser currentUser(OAuth2User principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return users.findByGoogleId((String) principal.getAttributes().get("sub"))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
