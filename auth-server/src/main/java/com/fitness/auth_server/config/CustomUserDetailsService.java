package com.fitness.auth_server.config;

import com.fitness.auth_server.entity.UserEntity;
import com.fitness.auth_server.model.Role;
import com.fitness.auth_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String password = userEntity.getPassword();
        if (password == null) {
            // Generate a random, unusable password for OAuth2 users
            password = UUID.randomUUID().toString();
        }

        return new User(userEntity.getEmail(),
                password,
                userEntity.isEnabled(),
                userEntity.isAccountNonExpired(),
                userEntity.isCredentialsNonExpired(),
                userEntity.isAccountNonLocked(),
                mapRolesToGrantedAuthorities(userEntity.getRoles()));
    }

    private Collection<GrantedAuthority> mapRolesToGrantedAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

//    public UserEntity getLoggedInUser() throws Exception {
//        Principal principal = SecurityContextHolder.getContext().getAuthentication();
//        return userRepository.findByEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
//    }
}

