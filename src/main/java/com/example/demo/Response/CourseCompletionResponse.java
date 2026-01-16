package com.example.demo.Response;

import lombok.Data;

@Data
public class CourseCompletionResponse {

    private String email;

    private String courseId;

    private Integer totalVideos;

    private Integer completedVideos;

    private Double completionPercentage;

    private Boolean completed;
}
