# interview-os-api (Spring Boot 3.3 / Java 21)

Owns Google OAuth 2.0 login and the interview data. Angular never talks to Google.

```
Angular  --login-->  Spring Boot  --OAuth 2.0-->  Google
                          |
                          +--session cookie--> Angular /dashboard
```

## Endpoints

| Method | Path                          | Auth | Purpose |
| ------ | ----------------------------- | ---- | ------- |
| GET    | `/oauth2/authorization/google?redirect_uri=…` | public | starts the Google flow |
| GET    | `/login/oauth2/code/google`   | public | Google callback (Spring Security) |
| GET    | `/api/auth/me`                | cookie | current profile, 401 when signed out |
| POST   | `/api/auth/logout`            | cookie | invalidates session, clears `JSESSIONID` |
| GET    | `/api/interviews/upcoming`    | cookie | upcoming interviews |
| GET    | `/api/interviews/stats`       | cookie | upcoming / this week / prepared counts |

## Google Cloud Console setup

1. APIs & Services → OAuth consent screen → External, add scopes `openid`, `email`, `profile`.
2. Credentials → Create OAuth client ID → **Web application**.
3. Authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
   (production: `https://api.your-domain.com/login/oauth2/code/google`).
4. Copy the client ID and secret.

## Database

```sql
CREATE DATABASE interviewos;
CREATE USER interviewos WITH PASSWORD 'interviewos';
GRANT ALL PRIVILEGES ON DATABASE interviewos TO interviewos;
```

Hibernate creates `app_user` and `interview` on first boot (`ddl-auto: update`).
Swap to Flyway before production.

## Run

```sh
export GOOGLE_CLIENT_ID=…
export GOOGLE_CLIENT_SECRET=…
export FRONTEND_BASE_URL=http://localhost:4200
./mvnw spring-boot:run       # or: mvn spring-boot:run
```

API on `http://localhost:8080`, Angular on `http://localhost:4200`.
CORS is already open to `FRONTEND_BASE_URL` with credentials allowed.

## Notes

- CSRF is disabled because the SPA uses only cookie-authenticated GETs plus a
  logout POST. If you add state-changing endpoints, enable
  `CookieCsrfTokenRepository.withHttpOnlyFalse()` and send `X-XSRF-TOKEN`.
- For cross-site deployments (different domains for UI and API) set
  `server.servlet.session.cookie.same-site=none` and serve both over HTTPS.
