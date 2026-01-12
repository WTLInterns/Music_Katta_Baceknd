
package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    
    @PostMapping("/create-course-with-image")
    public ResponseEntity<?> createCourseWithImage(
            @RequestParam("courseName") String courseName,
            @RequestParam("details") String details,
            @RequestParam("price") String price,
            @RequestParam("courseDuration") String courseDuration,
            @RequestParam("keywords") List<String> keywords,
            @RequestParam(value = "originalPrice", required = false) String originalPrice,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Courses course = new Courses();
            course.setCourseName(courseName);
            course.setDetails(details);
            course.setPrice(price);
            course.setCourseDuration(courseDuration);
            course.setKeywords(keywords);
            course.setOriginalPrice(originalPrice);
           
            course.setStatus(status != null ? status : "ACTIVE");
            
            // Set current date in dd-MM-yyyy format
            course.setPostDate(videoUploadService.getCurrentDateString());
            // Set current time in 12-hour format
            course.setPostTime(videoUploadService.getCurrentTimeString());
            
            Courses savedCourse = this.courseService.createCourseWithImage(course, image);
            return ResponseEntity.ok(savedCourse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading course: " + e.getMessage());
        }
    }

    @PutMapping("/update-course/{courseId}")
    public ResponseEntity<?> updateCourseWithImage(
            @PathVariable String courseId,
            @RequestParam("courseName") String courseName,
            @RequestParam("details") String details,
            @RequestParam("price") String price,
            @RequestParam(value = "originalPrice", required = false) String originalPrice,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Courses courseDetails = new Courses();
            courseDetails.setCourseName(courseName);
            courseDetails.setDetails(details);
            courseDetails.setPrice(price);
            courseDetails.setOriginalPrice(originalPrice);
            courseDetails.setStatus(status != null ? status : "ACTIVE");
            
            Courses updatedCourse = this.courseService.updateCourseWithImage(courseId, courseDetails, image);
            if (updatedCourse != null) {
                return ResponseEntity.ok(updatedCourse);
            } else {
                return ResponseEntity.status(404).body("Course not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating course: " + e.getMessage());
        }
    }

    @GetMapping("/all-courses")
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/get-course/{courseId}")
    public CourseResponse getCourseById(@PathVariable String courseId) {
        return courseService.getCourseById(courseId);
    }
    
    @DeleteMapping("/delete-course/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable String courseId) {
        try {
            this.courseService.deleteCourse(courseId);
            return ResponseEntity.ok("Course deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting course: " + e.getMessage());
        }
    }
}