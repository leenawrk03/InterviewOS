package dev.interviewos.api.calendar;

import dev.interviewos.api.user.AppUser;
import dev.interviewos.api.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final GoogleCalendarService calendarService;
    private final UserRepository userRepository;

    public CalendarController(
            GoogleCalendarService calendarService,
            UserRepository userRepository) {

        this.calendarService = calendarService;
        this.userRepository = userRepository;
    }

    @GetMapping("/events")
    public ResponseEntity<?> getUpcomingEvents(
            Principal principal) {

        try {
            AppUser user = userRepository
                    .findByGoogleId(principal.getName())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "InterviewOS user not found"
                            ));

            return ResponseEntity.ok(
                    calendarService.getUpcomingEvents(user)
            );

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }
}