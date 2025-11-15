package com.example.demo.Response;

import lombok.Data;

@Data
public class VideoResponse {
    private String videoId;
    private String title;
    private String description;
    private String videoUrl;
    private String postDate;
    private String postTime;
}