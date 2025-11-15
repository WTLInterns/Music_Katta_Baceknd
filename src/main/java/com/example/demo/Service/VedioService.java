package com.example.demo.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Courses;
import com.example.demo.Repo.CourseRepo;
import com.example.demo.Repo.VedioRepo;

import com.example.demo.Response.CourseWithVedio;
import com.example.demo.Response.VideoResponse;
import com.example.demo.Entity.Vedios;
import java.util.List;
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
            
            // Map videos to VideoResponse DTOs
            List<VideoResponse> videoResponses = course.getVedios().stream().map(video -> {
                VideoResponse videoResponse = new VideoResponse();
                videoResponse.setVideoId(video.getVedioId());
                videoResponse.setTitle(video.getTitle());
                videoResponse.setDescription(video.getVedioDescription());
                videoResponse.setVideoUrl(video.getVideoUrl());
                videoResponse.setPostDate(video.getPostDate());
                videoResponse.setPostTime(video.getPostTime());
                return videoResponse;
            }).collect(Collectors.toList());
            
            response.setVedios(videoResponses);
            return response;
        }
        return null;
    }
}
