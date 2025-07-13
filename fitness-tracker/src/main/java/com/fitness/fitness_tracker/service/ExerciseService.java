package com.fitness.fitness_tracker.service;

import com.fitness.fitness_tracker.entity.Exercise;
import com.fitness.fitness_tracker.model.Role;
import com.fitness.fitness_tracker.model.UserModel;
import com.fitness.fitness_tracker.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<Exercise> getAllExercises(String userId) {
        return exerciseRepository.findByCreatedByIn(Arrays.asList("SYSTEM", userId));
    }
    
    public List<Exercise> getExercisesByMuscleGroup(String userId, String muscleGroup) {
        return exerciseRepository.findByPrimaryMuscleGroupsInAndCreatedByIn(
                Arrays.asList(muscleGroup),
                Arrays.asList("SYSTEM", userId)
        );
    }

    public Exercise createExercise(Exercise exercise, UserModel user) {
        List<Role> roles = user.getRoles();
        if (roles.contains(Role.ROLE_ADMIN)) {
            exercise.setCreatedBy("SYSTEM");
        } else {
            exercise.setCreatedBy(String.valueOf(user.getUserId()));
        }
        return exerciseRepository.save(exercise);
    }
}
