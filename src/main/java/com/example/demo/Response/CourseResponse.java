package com.example.demo.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    

     private String courseId;

    private String courseName;

    private String details;

    private String postDate;

    private String postTime;

    private String price;
}
