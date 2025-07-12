package com.fitness.auth_server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.auth_server.dto.AuthResponseDto;
import com.fitness.auth_server.entity.UserEntity;
import com.fitness.auth_server.jwt.JwtUtil;
import com.fitness.auth_server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {


        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        UserEntity user = userService.findOrCreateGoogleUser(oAuth2User);

        String accessToken = jwtUtil.generateToken(email);
        String refreshToken = userService.updateRefreshToken(email);

        AuthResponseDto authResponseDTO = AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(authResponseDTO));
    }
}
