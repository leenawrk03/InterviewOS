package dev.interviewos.api.auth;

/** Payload for GET /api/auth/me — mirrors AuthUser in the Angular app. */
public record AuthUserResponse(String id, String email, String name, String pictureUrl) {
}
