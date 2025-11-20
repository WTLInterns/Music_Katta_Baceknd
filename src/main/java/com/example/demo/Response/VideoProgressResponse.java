package com.example.demo.Response;

import lombok.Data;

@Data
public class VideoProgressResponse {

    private String videoId;

    private String email;

    private Double lastPositionSeconds;

    private Double durationSeconds;

    private Boolean completed;
}
