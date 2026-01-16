package com.example.demo.Response;

import lombok.Data;

@Data
public class CourseTimeCompletionResponse {

    private String email;

    private String courseId;

    private Double totalDurationSeconds;

    private Double watchedSeconds;

    private Double remainingSeconds;

    private Double completionPercentage;

    private Boolean completed;
}
