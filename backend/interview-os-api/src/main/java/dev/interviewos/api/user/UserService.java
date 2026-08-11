package dev.interviewos.api.user;

import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> findByGoogleId(String googleId) {
        return repository.findByGoogleId(googleId);
    }

    @Transactional
    public AppUser upsertFromGoogle(String googleId, String email, String name, String pictureUrl) {
        AppUser user = repository.findByGoogleId(googleId).orElseGet(AppUser::new);
        user.setGoogleId(googleId);
        user.setEmail(email);
        user.setName(name);
        user.setPictureUrl(pictureUrl);
        user.setUpdatedAt(Instant.now());
        return repository.save(user);
    }
}
