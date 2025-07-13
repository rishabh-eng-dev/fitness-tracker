package com.fitness.fitness_tracker.controller;

import com.fitness.fitness_tracker.entity.Exercise;
import com.fitness.fitness_tracker.model.UserModel;
import com.fitness.fitness_tracker.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fitness/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises(
            @AuthenticationPrincipal UserModel userModel
    ) {
        return ResponseEntity.ok(exerciseService.getAllExercises(String.valueOf(userModel.getUserId())));
    }

    @GetMapping("/muscle-group/{muscleGroup}")
    public ResponseEntity<List<Exercise>> getByMuscleGroup(
            @AuthenticationPrincipal UserModel userModel,
            @PathVariable String muscleGroup
    ) {
        return ResponseEntity.ok(exerciseService.getExercisesByMuscleGroup(String.valueOf(userModel.getUserId()), muscleGroup));
    }

    @PostMapping
    public ResponseEntity<Exercise> createExercise(
            @AuthenticationPrincipal UserModel userModel,
            @RequestBody Exercise exercise
    ) {
        Exercise created = exerciseService.createExercise(exercise, userModel);
        return ResponseEntity.ok(created);
    }
}