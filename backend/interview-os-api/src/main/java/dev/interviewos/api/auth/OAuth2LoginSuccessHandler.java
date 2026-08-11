package dev.interviewos.api.auth;

import dev.interviewos.api.calendar.GoogleCalendarConnection;
import dev.interviewos.api.calendar.GoogleCalendarConnectionRepository;
import dev.interviewos.api.config.FrontendProperties;
import dev.interviewos.api.user.AppUser;
import dev.interviewos.api.user.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OAuth2LoginSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private static final String GOOGLE_REGISTRATION_ID = "google";

    private final UserService users;
    private final FrontendProperties frontend;

    private final OAuth2AuthorizedClientService authorizedClientService;

    private final GoogleCalendarConnectionRepository
            calendarConnectionRepository;

    public OAuth2LoginSuccessHandler(
            UserService users,
            FrontendProperties frontend,
            OAuth2AuthorizedClientService authorizedClientService,
            GoogleCalendarConnectionRepository
                    calendarConnectionRepository) {

        this.users = users;
        this.frontend = frontend;
        this.authorizedClientService = authorizedClientService;
        this.calendarConnectionRepository =
                calendarConnectionRepository;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        if (!(authentication.getPrincipal()
                instanceof OAuth2User principal)) {

            redirectToDashboard(
                    request,
                    response,
                    "error"
            );

            return;
        }

        String googleId =
                (String) principal.getAttributes().get("sub");

        String email =
                (String) principal.getAttributes().get("email");

        String name =
                (String) principal.getAttributes().get("name");

        String picture =
                (String) principal.getAttributes().get("picture");

        /*
         * 1. Create/update InterviewOS user
         */
        AppUser user =
                users.upsertFromGoogle(
                        googleId,
                        email,
                        name,
                        picture
                );

        /*
         * 2. Store InterviewOS user ID in session
         */
        request.getSession(true)
                .setAttribute(
                        "INTERVIEWOS_USER_ID",
                        user.getId()
                );

        /*
         * 3. Get Google OAuth authorized client
         */
        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        GOOGLE_REGISTRATION_ID,
                        authentication.getName()
                );

        /*
         * 4. Save Calendar authorization
         */
        if (authorizedClient != null) {

            saveCalendarConnection(
                    user,
                    authorizedClient
            );
        }

        /*
         * 5. Redirect to Angular
         */
        getRedirectStrategy().sendRedirect(
                request,
                response,
                resolveTarget(
                        request.getSession(false)
                )
        );
    }

    private void saveCalendarConnection(
            AppUser user,
            OAuth2AuthorizedClient authorizedClient) {

        if (authorizedClient.getAccessToken() == null) {
            return;
        }

        GoogleCalendarConnection connection =
                calendarConnectionRepository
                        .findByUserId(user.getId())
                        .orElseGet(
                                GoogleCalendarConnection::new
                        );

        connection.setUser(user);

        connection.setCalendarId("primary");

        connection.setAccessToken(
                authorizedClient
                        .getAccessToken()
                        .getTokenValue()
        );

        connection.setAccessTokenExpiresAt(
                authorizedClient
                        .getAccessToken()
                        .getExpiresAt()
        );

        /*
         * Google normally gives us a refresh token when
         * access_type=offline is used.
         */
        if (authorizedClient.getRefreshToken() != null) {

            connection.setRefreshToken(
                    authorizedClient
                            .getRefreshToken()
                            .getTokenValue()
            );
        }

        /*
         * Don't save the connection if we don't have
         * a refresh token yet.
         *
         * This protects us from accidentally creating
         * an unusable Calendar connection.
         */
        if (connection.getRefreshToken() != null
                && !connection.getRefreshToken().isBlank()) {

            calendarConnectionRepository.save(
                    connection
            );
        }
    }

    private String resolveTarget(HttpSession session) {

        Object memo =
                session == null
                        ? null
                        : session.getAttribute(
                        RedirectUriMemoFilter.SESSION_KEY
                );

        String fallback =
                frontend.baseUrl()
                        + frontend.defaultRedirectPath();

        if (memo == null) {
            return fallback;
        }

        if (session != null) {

            session.removeAttribute(
                    RedirectUriMemoFilter.SESSION_KEY
            );
        }

        String candidate =
                memo.toString();

        /*
         * Only redirect to our Angular frontend.
         */
        return candidate.startsWith(
                frontend.baseUrl()
        )
                ? candidate
                : fallback;
    }

    private void redirectToDashboard(
            HttpServletRequest request,
            HttpServletResponse response,
            String status)
            throws IOException {

        response.sendRedirect(
                frontend.baseUrl()
                        + "/dashboard?calendar="
                        + status
        );
    }
}