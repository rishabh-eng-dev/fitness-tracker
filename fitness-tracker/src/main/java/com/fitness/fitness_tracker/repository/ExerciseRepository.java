package com.fitness.fitness_tracker.repository;

import com.fitness.fitness_tracker.entity.Exercise;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExerciseRepository extends MongoRepository<Exercise, String> {
    List<Exercise> findByCreatedByIn(List<String> createdByList);
    List<Exercise> findByPrimaryMuscleGroupsInAndCreatedByIn(List<String> muscleGroups, List<String> createdByList);
    List<Exercise> findByPrimaryMuscleGroupsIn(List<String> muscleGroups);
}