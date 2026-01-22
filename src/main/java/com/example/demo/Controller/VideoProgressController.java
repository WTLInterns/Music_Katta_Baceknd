package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.VideoProgress;
import com.example.demo.Request.VideoProgressRequest;
import com.example.demo.Response.CourseCompletionResponse;
import com.example.demo.Response.CourseProgressResponse;
import com.example.demo.Response.CourseTimeCompletionResponse;
import com.example.demo.Response.VideoProgressResponse;
import com.example.demo.Service.VideoProgressService;

// @CrossOrigin(origins = "https://ddhavalmulay.com", allowCredentials = "true")
@RestController
@RequestMapping("/api/video-progress")
public class VideoProgressController {

    @Autowired
    private VideoProgressService videoProgressService;

    @PostMapping("/update")
    public ResponseEntity<VideoProgressResponse> updateProgress(@RequestBody VideoProgressRequest request) {
        VideoProgress progress = videoProgressService.saveOrUpdateProgress(
                request.getEmail(),
                request.getVideoId(),
                request.getCurrentTime(),
                request.getDuration(),
                request.getWatchedDeltaSeconds(),
                request.getCompleted());

        VideoProgressResponse response = new VideoProgressResponse();
        response.setVideoId(progress.getVideo().getVedioId());
        response.setEmail(progress.getUser().getEmail());
        response.setLastPositionSeconds(progress.getLastPositionSeconds());
        response.setDurationSeconds(progress.getDurationSeconds());
        response.setWatchedSeconds(progress.getWatchedSeconds());
        response.setCompleted(progress.getCompleted());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/course-completion")
    public ResponseEntity<CourseCompletionResponse> getCourseCompletion(
            @RequestParam("email") String email,
            @RequestParam("courseId") String courseId) {
        CourseCompletionResponse response = videoProgressService.getCourseCompletion(email, courseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course-time-completion")
    public ResponseEntity<CourseTimeCompletionResponse> getCourseTimeCompletion(
            @RequestParam("email") String email,
            @RequestParam("courseId") String courseId) {
        CourseTimeCompletionResponse response = videoProgressService.getCourseTimeCompletion(email, courseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course-progress")
    public ResponseEntity<CourseProgressResponse> getCourseProgress(
            @RequestParam("email") String email,
            @RequestParam("courseId") String courseId) {
        CourseProgressResponse response = videoProgressService.getCourseProgress(email, courseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get")
    public ResponseEntity<VideoProgressResponse> getProgress(
            @RequestParam("email") String email,
            @RequestParam("videoId") String videoId) {
        VideoProgress progress = videoProgressService.getProgress(email, videoId);
        if (progress == null) {
            return ResponseEntity.notFound().build();
        }

        VideoProgressResponse response = new VideoProgressResponse();
        response.setVideoId(progress.getVideo().getVedioId());
        response.setEmail(progress.getUser().getEmail());
        response.setLastPositionSeconds(progress.getLastPositionSeconds());
        response.setDurationSeconds(progress.getDurationSeconds());
        response.setWatchedSeconds(progress.getWatchedSeconds());
        response.setCompleted(progress.getCompleted());

        return ResponseEntity.ok(response);
    }
}
