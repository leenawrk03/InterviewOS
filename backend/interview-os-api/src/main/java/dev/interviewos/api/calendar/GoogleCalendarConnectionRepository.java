package dev.interviewos.api.calendar;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoogleCalendarConnectionRepository
        extends JpaRepository<GoogleCalendarConnection, UUID> {

    Optional<GoogleCalendarConnection> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}