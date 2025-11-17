package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.Courses;
import com.example.demo.Response.CourseResponse;
import com.example.demo.Service.CourseService;
import com.example.demo.Service.VideoUploadService;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;
    
    @Autowired
    private VideoUploadService videoUploadService;

    @PostMapping("/create-course")
    public Courses createCourse(@RequestBody Courses course) {
        // Set current date in dd-MM-yyyy format
        course.setPostDate(videoUploadService.getCurrentDateString());
        // Set current time in 12-hour format
        course.setPostTime(videoUploadService.getCurrentTimeString());
        return this.courseService.createCourse(course);
    }

    @GetMapping("/all-courses")
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/get-course/{courseId}")
    public CourseResponse getCourseById(@PathVariable String courseId) {
        return courseService.getCourseById(courseId);
    }
}
