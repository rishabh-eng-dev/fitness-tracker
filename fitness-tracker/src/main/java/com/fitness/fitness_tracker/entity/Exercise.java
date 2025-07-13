package com.fitness.fitness_tracker.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "exercises")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    @Id
    private String id;

    private String name;
    private String type; // e.g., "STRENGTH", "CARDIO"
    private String description;
    private List<String> primaryMuscleGroups;
    private String equipment;
    private String createdBy; // "SYSTEM" or userId
}