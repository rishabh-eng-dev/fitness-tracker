package com.fitness.fitness_tracker.service;

import com.fitness.fitness_tracker.entity.UserProfile;
import com.fitness.fitness_tracker.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile addProfile(String userId, UserProfile profileRequest) {
        Optional<UserProfile> existing = userProfileRepository.findByUserId(userId);
        existing.ifPresent(userProfile -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Resource already exists");
        });
        UserProfile profile = profileRequest.toBuilder()
                .userId(userId)
                .createdAt(existing.map(UserProfile::getCreatedAt).orElse(Instant.now()))
                .updatedAt(Instant.now())
                .build();

        return userProfileRepository.save(profile);
    }

    public UserProfile updateProfile(String userId, UserProfile profileRequest) {
        Optional<UserProfile> existing = userProfileRepository.findByUserId(userId);
        UserProfile profile = profileRequest.toBuilder()
                .userId(userId)
                .createdAt(existing.map(UserProfile::getCreatedAt).orElse(Instant.now()))
                .updatedAt(Instant.now())
                .build();
        existing.ifPresent(userProfile -> profile.setId(userProfile.getId()));
        return userProfileRepository.save(profile);
    }

    public Optional<UserProfile> getProfile(String userId) {
        return userProfileRepository.findByUserId(userId);
    }
}