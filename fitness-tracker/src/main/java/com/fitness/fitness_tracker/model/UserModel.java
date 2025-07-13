package com.fitness.fitness_tracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class UserModel {
    private Long userId;
    private String email;
    private List<Role> roles;
}
