package com.fitness.auth_server.controller;

import com.fitness.auth_server.dto.AuthResponseDto;
import com.fitness.auth_server.dto.LoginDto;
import com.fitness.auth_server.dto.RefreshTokenRequestDto;
import com.fitness.auth_server.dto.RegisterDto;
import com.fitness.auth_server.entity.UserEntity;
import com.fitness.auth_server.jwt.JwtUtil;
import com.fitness.auth_server.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    private ResponseEntity<String> register(@Valid @RequestBody RegisterDto registerDto) {
        try {
            userService.createUser(registerDto);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    private ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        Authentication authentication = getAuthentication(loginDto.getEmail(), loginDto.getPassword());
        AuthResponseDto authResponseDTO = getAuthResponseDto(loginDto.getEmail());
        return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        if (userService.isRefreshTokenValid(refreshTokenRequestDto)) {
            AuthResponseDto authResponseDTO = getAuthResponseDto(refreshTokenRequestDto.getEmail());
            return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }
    }

    private Authentication getAuthentication(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        if (authentication == null) {
            throw new UsernameNotFoundException("Invalid user request!");
        }
        return authentication;
    }

    private AuthResponseDto getAuthResponseDto(String email) {
        UserEntity user = userService.getUserByEmail(email);
        String accessToken = jwtUtil.generateToken(user.getId(), email);
        String refreshToken = userService.updateRefreshToken(email);
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
