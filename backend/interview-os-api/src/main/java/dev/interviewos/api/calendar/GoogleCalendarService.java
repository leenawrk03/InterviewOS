package dev.interviewos.api.calendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;

import dev.interviewos.api.email.EmailService;
import dev.interviewos.api.user.AppUser;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;
import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleCalendarService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    private static final String APPLICATION_NAME = "InterviewOS";

    private final GoogleCalendarConnectionRepository connectionRepository;
    private final EmailService emailService;

    public GoogleCalendarService(
            GoogleCalendarConnectionRepository connectionRepository,
            EmailService emailService) {
        this.connectionRepository = connectionRepository;
        this.emailService = emailService;
    }

    public List<CalendarInterviewResponse> getUpcomingInterviewEvents(
            AppUser user) throws Exception {

        Events events = getUpcomingEvents(user);

        if (events.getItems() == null) {
            return List.of();
        }

        List<CalendarInterviewResponse> result = new ArrayList<>();

        for (Event event : events.getItems()) {

            String startTime = null;
            String endTime = null;

            if (event.getStart() != null) {

                if (event.getStart().getDateTime() != null) {
                    startTime =
                            event.getStart()
                                    .getDateTime()
                                    .toStringRfc3339();
                } else if (event.getStart().getDate() != null) {
                    startTime =
                            event.getStart()
                                    .getDate()
                                    .toString();
                }
            }

            if (event.getEnd() != null) {

                if (event.getEnd().getDateTime() != null) {
                    endTime =
                            event.getEnd()
                                    .getDateTime()
                                    .toStringRfc3339();
                } else if (event.getEnd().getDate() != null) {
                    endTime =
                            event.getEnd()
                                    .getDate()
                                    .toString();
                }
            }

            result.add(
                    new CalendarInterviewResponse(
                            event.getId(),
                            event.getSummary(),
                            event.getDescription(),
                            startTime,
                            endTime,
                            event.getHtmlLink()
                    )
            );
        }

        return result;
    }

    public Events getUpcomingEvents(AppUser user) throws Exception {

        GoogleCalendarConnection connection =
                connectionRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Google Calendar is not connected"
                                ));

        GoogleCredential credential =
                new GoogleCredential.Builder()
                        .setTransport(
                                GoogleNetHttpTransport
                                        .newTrustedTransport()
                        )
                        .setJsonFactory(
                                GsonFactory.getDefaultInstance()
                        )
                        .setClientSecrets(clientId, clientSecret)
                        .build();

        credential.setAccessToken(connection.getAccessToken());
        credential.setRefreshToken(connection.getRefreshToken());

        Calendar calendar =
                new Calendar.Builder(
                        GoogleNetHttpTransport
                                .newTrustedTransport(),
                        GsonFactory
                                .getDefaultInstance(),
                        credential
                )
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        Events events = calendar.events()
                .list(connection.getCalendarId())
                .setTimeMin(
                        new com.google.api.client.util.DateTime(
                                Instant.now().toEpochMilli()
                        )
                )
                .setMaxResults(20)
                .setSingleEvents(true)
                .setOrderBy("startTime")
                .execute();

        /*
         * Fire the notification email in the same request as the fetch,
         * so "we fetched the events" and "an email was sent" always stay
         * in sync. sendUpcomingEventsEmail() swallows its own failures,
         * so a broken mail server never breaks the calendar fetch.
         */
        emailService.sendUpcomingEventsEmail(
                user,
                events.getItems() == null
                        ? List.of()
                        : events.getItems()
        );

        return events;
    }

}