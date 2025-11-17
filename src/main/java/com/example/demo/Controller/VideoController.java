package com.example.demo.Controller;

import com.example.demo.Entity.Vedios;
import com.example.demo.Request.VideoUploadRequest;
import com.example.demo.Response.CourseWithVedio;
import com.example.demo.Response.VideoResponse;
// import com.example.demo.Response.CourseResponse;
import com.example.demo.Service.VideoUploadService;
import com.example.demo.Service.CourseService;
import com.example.demo.Service.VedioService;
import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private VideoUploadService videoUploadService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private VedioService vedioService;

    @PostMapping("/upload/{courseId}")
    public ResponseEntity<?> uploadVideo(@ModelAttribute VideoUploadRequest request,
            @PathVariable String courseId) {
        try {
            Map uploadResult = videoUploadService.uploadVideo(request.getVideo());

            String videoUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            Vedios video = new Vedios();
            video.setTitle(request.getTitle());
            video.setVedioDescription(request.getDescription());
            video.setVideoUrl(videoUrl);
            video.setCloudinaryPublicId(publicId);
            video.setPostDate(videoUploadService.getCurrentDateString());
            video.setPostTime(videoUploadService.getCurrentTimeString());

            courseService.addVideoToCourse(courseId, video);

            VideoResponse response = new VideoResponse();
            response.setVideoId(video.getVedioId());
            response.setTitle(video.getTitle());
            response.setDescription(video.getVedioDescription());
            response.setVideoUrl(videoUrl);
            response.setPostDate(video.getPostDate());
            response.setPostTime(video.getPostTime());
            response.setCourseId(courseId);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error uploading video: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable String videoId) {
        try {
            courseService.deleteVideo(videoId);
            return ResponseEntity.ok("Video deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting video: " + e.getMessage());
        }
    }

    // @GetMapping("/getVedioWithCourse/{courseId}")
    // public List<CourseWithVedio> getVedioWithCourse(@PathVariable String courseId) {
    //     return vedioService.getAllCoursesWithVedios(courseId);
    // }
}