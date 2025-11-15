package com.example.demo.Response;

import java.util.List;

import com.example.demo.Response.VideoResponse;

import lombok.Data;
@Data

public class CourseWithVedio {
    private String courseId;
    private String courseName;
    private String details;
    private String postDate;
    private String postTime;
    private String price;
    private List<VideoResponse> vedios;
}
