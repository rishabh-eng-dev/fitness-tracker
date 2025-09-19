package com.fitness.fitness_tracker.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Document(collection = "user_profiles")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    private String id;

    private String userId; // Reference to Auth Server user

    private PersonalDetails personalDetails;
    private Goals goals;
    private Preferences preferences;

    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonalDetails {
        private Double weight;
        private Double height;
        private Date dateOfBirth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Goals {
        private Double targetWeight;
        private Integer weeklyWorkoutFrequency;
        private List<String> activeGoals;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Preferences {
        private Units units;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Units {
        private String weight;   // "kg" or "lbs"
        private String distance; // "km" or "miles"
        private String length; // "cm" or inches
    }
}
