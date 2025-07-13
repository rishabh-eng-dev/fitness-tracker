package com.fitness.auth_server.service;

import com.fitness.auth_server.dto.RefreshTokenRequestDto;
import com.fitness.auth_server.dto.RegisterDto;
import com.fitness.auth_server.entity.UserEntity;
import com.fitness.auth_server.model.AuthProvider;
import com.fitness.auth_server.model.Role;
import com.fitness.auth_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Value("${refresh_token_duration}")
    private Integer REFRESH_TOKEN_DURATION;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createUser(RegisterDto registerDto) throws Exception {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new Exception("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setProvider(AuthProvider.LOCAL);
        user.setEnabled(true);

        userRepository.save(user);
    }

    public String updateRefreshToken(String email) {
        String refreshToken = UUID.randomUUID().toString();

        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setHashedRefreshToken(passwordEncoder.encode(refreshToken));
        user.setHashedRefreshTokenExpiry(new Date(System.currentTimeMillis() + REFRESH_TOKEN_DURATION));
        userRepository.save(user);

        return refreshToken;
    }

    public boolean isRefreshTokenValid(RefreshTokenRequestDto refreshTokenRequestDto) {
        UserEntity user = getUserByEmail(refreshTokenRequestDto.getEmail());

        String hashedRefreshToken = user.getHashedRefreshToken();
        if (hashedRefreshToken == null || hashedRefreshToken.isEmpty()) {
            return false;
        }
        if (user.getHashedRefreshTokenExpiry() == null || user.getHashedRefreshTokenExpiry().before(new Date())) {
            return false;
        }
        return passwordEncoder.matches(refreshTokenRequestDto.getToken(), hashedRefreshToken);
    }

    public UserEntity findOrCreateGoogleUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String googleProviderId = oAuth2User.getAttribute("sub");


        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email not found from OAuth2 provider");
        }
        if (googleProviderId == null || googleProviderId.isBlank()) {
            throw new IllegalArgumentException("Provider ID (sub) not found from OAuth2 provider");
        }

        String googleFirstName = oAuth2User.getAttribute("given_name");
        String googleLastName = oAuth2User.getAttribute("family_name");
        String googlePictureUrl = oAuth2User.getAttribute("picture");

        Optional<UserEntity> existingUser = userRepository.findByEmail(email);
        UserEntity user;

        if (existingUser.isPresent()) {
            user = existingUser.get();

            if (user.getProvider() == AuthProvider.LOCAL) {
                user.setProvider(AuthProvider.GOOGLE);
            }
            user.setProviderId(googleProviderId);
            user.setFirstName(googleFirstName != null ? googleFirstName : user.getFirstName());
            user.setLastName(googleLastName != null ? googleLastName : user.getLastName());
            if (googlePictureUrl != null) {
                user.setImageUrl(googlePictureUrl);
            }
        } else {
            user = new UserEntity();
            user.setEmail(email);
            user.setProvider(AuthProvider.GOOGLE);
            user.setProviderId(googleProviderId);
            user.setRoles(Collections.singleton((Role.ROLE_USER)));

            user.setFirstName(googleFirstName != null ? googleFirstName : "DefaultFirstName");
            user.setLastName(googleLastName != null ? googleLastName : "DefaultLastName");
            if (googlePictureUrl != null) {
                user.setImageUrl(googlePictureUrl);
            }

            user.setEnabled(true);
        }

        return userRepository.save(user);
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new BadCredentialsException(""));
    }
}
