package dev.interviewos.api.auth;

import dev.interviewos.api.user.AppUser;
import dev.interviewos.api.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService users;

    public AuthController(UserService users) {
        this.users = users;
    }

    /** 200 with the profile when signed in, 401 otherwise. Logout is handled by Spring Security. */
    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> me(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String googleId = (String) principal.getAttributes().get("sub");
        return users.findByGoogleId(googleId)
            .map(this::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(401).build());
    }

    private AuthUserResponse toResponse(AppUser user) {
        return new AuthUserResponse(
            user.getId().toString(), user.getEmail(), user.getName(), user.getPictureUrl());
    }
}
