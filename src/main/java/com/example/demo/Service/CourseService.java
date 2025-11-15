package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Courses;
import com.example.demo.Entity.Vedios;
import com.example.demo.Repo.CourseRepo;
import com.example.demo.Repo.VedioRepo;

@Service
public class CourseService {

    @Autowired
    private CourseRepo courseRepo;
    
    @Autowired
    private VedioRepo vedioRepo;
    
    @Autowired
    private VideoUploadService videoUploadService;

    public Courses createCourse(Courses course) {
        return this.courseRepo.save(course);
    }

    public void deleteCourse(String courseId) {
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

        
}
