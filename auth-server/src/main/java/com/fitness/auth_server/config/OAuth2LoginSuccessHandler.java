package com.fitness.auth_server.config;

import com.fitness.auth_server.entity.UserEntity;
import com.fitness.auth_server.jwt.JwtUtil;
import com.fitness.auth_server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${frontend.host}")
    private String frontendHost;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {


        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        UserEntity user = userService.findOrCreateGoogleUser(oAuth2User);

        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = userService.updateRefreshToken(user.getEmail());

        String redirectUrl = String.format(
                "%s/auth/success?accessToken=%s&refreshToken=%s",
                frontendHost, accessToken, refreshToken
        );

        response.sendRedirect(redirectUrl);
    }
}
