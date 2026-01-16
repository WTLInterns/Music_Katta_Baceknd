package com.example.demo.Response;

import java.util.List;

import lombok.Data;

@Data
public class CourseProgressResponse {

    private String email;

    private String courseId;

    private Double totalDurationSeconds;

    private Double watchedSeconds;

    private Double remainingSeconds;

    private Double completionPercentage;

    private Boolean completed;

    private List<CourseVideoProgressItem> videos;
}
