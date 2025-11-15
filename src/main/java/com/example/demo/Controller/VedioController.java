package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Response.CourseWithVedio;
import com.example.demo.Service.VedioService;

@RestController
@RequestMapping("/api/videos")
public class VedioController {

    @Autowired
    private VedioService vedioService;

    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<?> getVideosByCourseId(@PathVariable String courseId) {
        CourseWithVedio response = vedioService.getVideosByCourseId(courseId);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}