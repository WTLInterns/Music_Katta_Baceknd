package com.example.demo.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Courses;
import com.example.demo.Repo.CourseRepo;
import com.example.demo.Repo.VedioRepo;

import com.example.demo.Response.CourseWithVedio;
import com.example.demo.Response.VideoResponse;
import com.example.demo.Entity.Vedios;
import java.util.stream.Collectors;

@Service
public class VedioService {

    @Autowired
    private VedioRepo vedioRepo;

    @Autowired
    private CourseRepo courseRepo;

    public CourseWithVedio getVideosByCourseId(String courseId) {
    Courses course = courseRepo.findById(courseId).orElse(null);
    if (course != null) {
        CourseWithVedio response = new CourseWithVedio();
        response.setCourseId(course.getCourseId());
        response.setCourseName(course.getCourseName());
        response.setDetails(course.getDetails());
        response.setPostDate(course.getPostDate());
        response.setPostTime(course.getPostTime());
        response.setPrice(course.getPrice());
        
        // Define date formatter for parsing
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        
        // Map videos to VideoResponse DTOs and sort by date and time in descending order
        List<VideoResponse> videoResponses = course.getVedios().stream()
            .sorted((v1, v2) -> {
                // Parse dates
                LocalDate date1 = LocalDate.parse(v1.getPostDate(), dateFormatter);
                LocalDate date2 = LocalDate.parse(v2.getPostDate(), dateFormatter);
                
                // Compare dates first
                int dateCompare = date2.compareTo(date1); // Reverse order for descending
                if (dateCompare != 0) {
                    return dateCompare;
                }
                
                // If dates are equal, compare times
                LocalTime time1 = LocalTime.parse(v1.getPostTime(), timeFormatter);
                LocalTime time2 = LocalTime.parse(v2.getPostTime(), timeFormatter);
                return time2.compareTo(time1); // Reverse order for descending
            })
            .map(video -> {
                VideoResponse videoResponse = new VideoResponse();
                videoResponse.setVideoId(video.getVedioId());
                videoResponse.setTitle(video.getTitle());
                videoResponse.setDescription(video.getVedioDescription());
                videoResponse.setVideoUrl(video.getVideoUrl());
                videoResponse.setPostDate(video.getPostDate());
                videoResponse.setPostTime(video.getPostTime());
                return videoResponse;
            }).collect(Collectors.toList());

        // Reverse the list order
        Collections.reverse(videoResponses);
        
        response.setVedios(videoResponses);
        return response;
    }
    return null;
}
}
