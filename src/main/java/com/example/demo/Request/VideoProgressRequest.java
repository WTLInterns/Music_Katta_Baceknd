package com.example.demo.Request;

import lombok.Data;

@Data
public class VideoProgressRequest {

    private String email;

    private String videoId;

    private Double currentTime;

    private Double duration;

    private Double watchedDeltaSeconds;

    private Boolean completed;
}
