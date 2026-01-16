package com.example.demo.Response;

import lombok.Data;

@Data
public class CourseVideoProgressItem {

    private String videoId;

    private String title;

    private String videoUrl;

    private Double lastPositionSeconds;

    private Double durationSeconds;

    private Double watchedSeconds;

    private Double completionPercentage;

    private Boolean completed;
}
