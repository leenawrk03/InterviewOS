package dev.interviewos.api.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Remembers the SPA's ?redirect_uri= when the login flow starts, so the success
 * handler can send the user back to the exact page they came from.
 */
public class RedirectUriMemoFilter extends OncePerRequestFilter {

    public static final String SESSION_KEY = "INTERVIEWOS_POST_LOGIN_REDIRECT";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/oauth2/authorization/")) {
            String redirectUri = request.getParameter("redirect_uri");
            if (redirectUri != null && !redirectUri.isBlank()) {
                request.getSession(true).setAttribute(SESSION_KEY, redirectUri);
            }
        }
        chain.doFilter(request, response);
    }
}
