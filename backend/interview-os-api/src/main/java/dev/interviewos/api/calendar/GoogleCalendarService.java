package dev.interviewos.api.calendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;

import dev.interviewos.api.user.AppUser;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;

@Service
public class GoogleCalendarService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    private static final String APPLICATION_NAME = "InterviewOS";

    private final GoogleCalendarConnectionRepository connectionRepository;

    public GoogleCalendarService(
            GoogleCalendarConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    public Events getUpcomingEvents(AppUser user) throws Exception {

        GoogleCalendarConnection connection =
                connectionRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Google Calendar is not connected"
                                ));

        /*
         * Build Google credential using the OAuth tokens
         * that we already received during Google login.
         */
        GoogleCredential credential =
                new GoogleCredential.Builder()
                        .setTransport(
                                GoogleNetHttpTransport.newTrustedTransport()
                        )
                        .setJsonFactory(
                                GsonFactory.getDefaultInstance()
                        )
                        .setClientSecrets(clientId, clientSecret)
                        .build();
        credential.setAccessToken(connection.getAccessToken())
                .setRefreshToken(connection.getRefreshToken());

        Calendar calendar =
                new Calendar.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        credential
                )
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        return calendar.events()
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
    }
}