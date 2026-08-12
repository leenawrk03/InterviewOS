package dev.interviewos.api.interview;

import dev.interviewos.api.calendar.CalendarInterviewResponse;
import dev.interviewos.api.calendar.GoogleCalendarService;
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

    private final GoogleCalendarService calendarService;
    private final UserService users;

    public InterviewController(
            GoogleCalendarService calendarService,
            UserService users) {

        this.calendarService = calendarService;
        this.users = users;
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<CalendarInterviewResponse>> upcoming(
            @AuthenticationPrincipal OAuth2User principal) {

        AppUser user = currentUser(principal);

        try {
            return ResponseEntity.ok(
                    calendarService.getUpcomingInterviewEvents(user)
            );
        } catch (Exception e) {
            e.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to fetch Google Calendar events",
                    e
            );
        }
    }

    private AppUser currentUser(OAuth2User principal) {

        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED
            );
        }

        String googleId =
                (String) principal.getAttributes().get("sub");

        return users.findByGoogleId(googleId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED
                        )
                );
    }
}