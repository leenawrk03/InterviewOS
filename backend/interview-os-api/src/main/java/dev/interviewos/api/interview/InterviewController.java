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
import com.google.api.services.calendar.model.Events;
import dev.interviewos.api.calendar.GoogleCalendarService;
import dev.interviewos.api.interview.InterviewDtos.InterviewResponse;

import java.time.Instant;
import java.util.List;

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
    public ResponseEntity<List<InterviewResponse>> upcoming(
            @AuthenticationPrincipal OAuth2User principal) throws Exception {

        AppUser user = currentUser(principal);

        Events events = calendarService.getUpcomingEvents(user);

        List<InterviewResponse> response = events.getItems()
                .stream()
                .map(event -> {

                    String startsAt = null;

                    if (event.getStart() != null) {

                        if (event.getStart().getDateTime() != null) {
                            startsAt = Instant.ofEpochMilli(
                                    event.getStart()
                                            .getDateTime()
                                            .getValue()
                            ).toString();

                        } else if (event.getStart().getDate() != null) {
                            startsAt = event.getStart()
                                    .getDate()
                                    .toString();
                        }
                    }

                    return new InterviewResponse(
                            event.getId(),
                            event.getSummary(),
                            "",
                            "",
                            startsAt,
                            event.getDescription(),
                            event.getHtmlLink(),
                            "Google Calendar",
                            false
                    );
                })
                .toList();

        return ResponseEntity.ok(response);
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