# InterviewOS — single Render web service.
# The Angular bundle is baked into the Spring Boot jar so the SPA and the API
# share one origin: no CORS, and JSESSIONID stays a first-party cookie.

# ---------- 1. Angular ----------
FROM node:20-alpine AS ui
WORKDIR /ui
COPY frontend/interview-os-ui/package.json frontend/interview-os-ui/package-lock.json ./
RUN npm ci
COPY frontend/interview-os-ui/ ./
RUN npm run build

# ---------- 2. Spring Boot ----------
FROM maven:3.9-eclipse-temurin-21 AS api
WORKDIR /api
COPY backend/interview-os-api/pom.xml ./
# Best-effort dependency prefetch so Render's layer cache skips the download on
# rebuilds. Failure here is harmless: the package step below refetches.
RUN mvn -B dependency:go-offline || true
COPY backend/interview-os-api/src ./src
COPY --from=ui /ui/dist/interview-os-ui/browser/ ./src/main/resources/static/
RUN mvn -B -DskipTests package

# ---------- 3. Runtime ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=api /api/target/interview-os-api-*.jar app.jar
COPY entrypoint.sh ./entrypoint.sh
RUN chmod +x entrypoint.sh
# Render's smaller instances are memory-capped; keep the heap inside the box.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k"
EXPOSE 8080
ENTRYPOINT ["./entrypoint.sh"]
