package com.fitness.fitness_tracker.controller;

import com.fitness.fitness_tracker.entity.UserProfile;
import com.fitness.fitness_tracker.model.UserModel;
import com.fitness.fitness_tracker.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/fitness/profile")
@RequiredArgsConstructor
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // Add or update user profile
    @PostMapping
    public ResponseEntity<UserProfile> addProfile(
            @AuthenticationPrincipal UserModel userModel,
            @RequestBody UserProfile profileRequest
    ) {
        UserProfile saved = userProfileService.addProfile(String.valueOf(userModel.getUserId()), profileRequest);
        return ResponseEntity.created(URI.create("api/v1/fitness/profile")).body(saved);
    }

    @PatchMapping
    public ResponseEntity<UserProfile> updateProfile(
            @AuthenticationPrincipal UserModel userModel,
            @RequestBody UserProfile profileRequest
    ) {
        UserProfile saved = userProfileService.updateProfile(String.valueOf(userModel.getUserId()), profileRequest);
        return ResponseEntity.ok(saved);
    }

    // Get user profile
    @GetMapping
    public ResponseEntity<UserProfile> getProfile(
            @AuthenticationPrincipal UserModel userModel
    ) {
        return userProfileService.getProfile(String.valueOf(userModel.getUserId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}