# InterviewOS — Phase 1

```
Angular  ──login──▶  Spring Boot  ──OAuth 2.0──▶  Google
                          │
                          ▼
                     Spring Boot  ──▶  Angular Dashboard
```

Google sign-in is owned entirely by **Spring Boot** (`spring-boot-starter-oauth2-client`
+ Spring Security). Angular only redirects to the API and then reads
`GET /api/auth/me` with the session cookie. No Supabase anywhere.

## Stack

| Layer     | Tech |
| --------- | ---- |
| Frontend  | Angular 18 (standalone components + signals), TypeScript |
| Backend   | Java 21, Spring Boot 3.3, Spring Security OAuth 2.0 |
| Database  | PostgreSQL 15+ (JPA/Hibernate) |
| Build     | npm / Maven |

## Layout

```
interview-os/
├── frontend/interview-os-ui/
│   └── src/app/
│       ├── core/{auth,guards,interceptors,services}
│       ├── features/{login,dashboard}
│       ├── shared/{components,models}
│       ├── app.routes.ts
│       └── app.config.ts
└── backend/interview-os-api/
    ├── pom.xml
    └── src/main/java/dev/interviewos/api/
        ├── auth/       (success handler, /api/auth/me, redirect memo filter)
        ├── config/     (SecurityConfig, CORS, frontend properties)
        ├── interview/  (entity, repository, service, controller)
        └── user/       (AppUser upserted from the Google profile)
```

## Run it

1. **Backend** — see `backend/interview-os-api/README.md` for Google Console and
   PostgreSQL setup, then:
   ```sh
   cd backend/interview-os-api
   export GOOGLE_CLIENT_ID=… GOOGLE_CLIENT_SECRET=…
   mvn spring-boot:run          # :8080
   ```
2. **Frontend**
   ```sh
   cd frontend/interview-os-ui
   npm install
   npm start                    # :4200
   ```
3. Open `http://localhost:4200` → *Continue with Google* → Google consent →
   back on `/dashboard` signed in.

`frontend/interview-os-ui/src/environments/environment.ts` holds the single knob
(`apiBaseUrl`, default `http://localhost:8080`).

## Flow in code

| Step | Where |
| ---- | ----- |
| Button click | `features/login/login.component.ts` |
| Redirect to API | `core/auth/auth.service.ts` → `/oauth2/authorization/google?redirect_uri=…` |
| Google exchange | Spring Security `oauth2Login()` in `config/SecurityConfig.java` |
| Profile upsert + return redirect | `auth/OAuth2LoginSuccessHandler.java` |
| Session read | `auth/AuthController.java` ⇄ `core/auth/auth.service.ts` |
| Route protection | `core/guards/auth.guard.ts`, `guest.guard.ts` |
| Cookie on every call | `core/interceptors/auth.interceptor.ts` (`withCredentials`) |
