package com.example.demo.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Entity.Courses;
import com.example.demo.Entity.Vedios;
import com.example.demo.Repo.CourseRepo;
import com.example.demo.Repo.VedioRepo;
import com.example.demo.Response.CourseResponse;

@Service
public class CourseService {

    @Autowired
    private CourseRepo courseRepo;
    
    @Autowired
    private VedioRepo vedioRepo;
    
    @Autowired
    private VideoUploadService videoUploadService;
    
    @Autowired
    private CloudinaryService cloudinaryService;

    // Helper method to calculate discount percentage
    private String calculateDiscountPercentage(String originalPrice, String discountedPrice) {
        try {
            double original = Double.parseDouble(originalPrice);
            double discounted = Double.parseDouble(discountedPrice);
            
            if (original <= 0) return "0";
            
            double discount = ((original - discounted) / original) * 100;
            return String.format("%.0f", discount);
        } catch (NumberFormatException e) {
            return "0";
        }
    }

    public Courses createCourse(Courses course) {
        // Calculate discount percentage if both prices are provided
        if (course.getOriginalPrice() != null && course.getPrice() != null) {
            course.setDiscountPercentage(calculateDiscountPercentage(course.getOriginalPrice(), course.getPrice()));
        }
        
        // Set default status to ACTIVE if not provided
        if (course.getStatus() == null || course.getStatus().isEmpty()) {
            course.setStatus("ACTIVE");
        }
        
        return this.courseRepo.save(course);
    }

    public Courses createCourseWithImage(Courses course, MultipartFile imageFile) throws IOException {
        // Calculate discount percentage if both prices are provided
        if (course.getOriginalPrice() != null && course.getPrice() != null) {
            course.setDiscountPercentage(calculateDiscountPercentage(course.getOriginalPrice(), course.getPrice()));
        }
        
        // Set default status to ACTIVE if not provided
        if (course.getStatus() == null || course.getStatus().isEmpty()) {
            course.setStatus("ACTIVE");
        }
        
        if (imageFile != null && !imageFile.isEmpty()) {
            // Upload image to Cloudinary
            Map uploadResult = cloudinaryService.upload(imageFile);
            course.setCourseImageUrl((String) uploadResult.get("secure_url"));
            course.setCourseImagePublicId((String) uploadResult.get("public_id"));
        }
        return this.courseRepo.save(course);
    }

    public Courses updateCourseWithImage(String courseId, Courses courseDetails, MultipartFile imageFile) throws IOException {
        Courses course = courseRepo.findById(courseId).orElse(null);
        if (course != null) {
            course.setCourseName(courseDetails.getCourseName());
            course.setDetails(courseDetails.getDetails());
            course.setPrice(courseDetails.getPrice());
            course.setOriginalPrice(courseDetails.getOriginalPrice());
            course.setStatus(courseDetails.getStatus());
            
            // Calculate discount percentage if both prices are provided
            if (courseDetails.getOriginalPrice() != null && courseDetails.getPrice() != null) {
                course.setDiscountPercentage(calculateDiscountPercentage(courseDetails.getOriginalPrice(), courseDetails.getPrice()));
            }
            
            // If a new image is provided, update it
            if (imageFile != null && !imageFile.isEmpty()) {
                // Delete old image from Cloudinary if it exists
                if (course.getCourseImagePublicId() != null) {
                    try {
                        cloudinaryService.delete(course.getCourseImagePublicId());
                    } catch (IOException e) {
                        // Log the error but continue with the update
                        e.printStackTrace();
                    }
                }
                
                // Upload new image to Cloudinary
                Map uploadResult = cloudinaryService.upload(imageFile);
                course.setCourseImageUrl((String) uploadResult.get("secure_url"));
                course.setCourseImagePublicId((String) uploadResult.get("public_id"));
            }
            
            return this.courseRepo.save(course);
        }
        return null;
    }

    public void deleteCourse(String courseId) throws IOException {
        Courses course = courseRepo.findById(courseId).orElse(null);
        if (course != null && course.getCourseImagePublicId() != null) {
            // Delete image from Cloudinary
            cloudinaryService.delete(course.getCourseImagePublicId());
        }
        this.courseRepo.deleteById(courseId);
    }
    
    public Courses addVideoToCourse(String courseId, Vedios video) {
        Courses course = courseRepo.findById(courseId).orElse(null);
        if (course != null) {
            video.setCourse(course);
            vedioRepo.save(video);
            return course;
        }
        return null;
    }
    
    public void deleteVideo(String videoId) throws Exception {
        Vedios video = vedioRepo.findById(videoId).orElse(null);
        if (video != null) {
            // Delete from Cloudinary first
            if (video.getCloudinaryPublicId() != null) {
                videoUploadService.deleteVideo(video.getCloudinaryPublicId());
            }
            // Then delete from database
            vedioRepo.deleteById(videoId);
        }
    }


    public List<CourseResponse> getAllCourses() {
        return courseRepo.findAll().stream()
                .map(course -> new CourseResponse(
                        course.getCourseId(),
                        course.getCourseName(),
                        course.getDetails(),
                        course.getPostDate(),
                        course.getPostTime(),
                        course.getPrice(),
                        course.getOriginalPrice(),
                        course.getDiscountPercentage(),
                        course.getStatus(),
                        course.getCourseImageUrl(),
                        course.getCourseDuration(),
                        course.getKeywords() != null ? String.join(",", course.getKeywords()) : null))
                .collect(Collectors.toList());
    }


    public CourseResponse getCourseById(String courseId) {
        Courses course = courseRepo.findById(courseId).orElse(null);
        if (course != null) {
            return new CourseResponse(
                    course.getCourseId(),
                    course.getCourseName(),
                    course.getDetails(),
                    course.getPostDate(),
                    course.getPostTime(),
                    course.getPrice(),
                    course.getOriginalPrice(),
                    course.getDiscountPercentage(),
                    course.getStatus(),
                    course.getCourseImageUrl(),
                    course.getCourseDuration(),
                    course.getKeywords() != null ? String.join(",", course.getKeywords()) : null);
        }
        return null;
    }
}