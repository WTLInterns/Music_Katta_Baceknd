package com.example.demo.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Courses;
import com.example.demo.Entity.User;
import com.example.demo.Entity.Vedios;
import com.example.demo.Entity.VideoProgress;
import com.example.demo.Repo.CourseRepo;
import com.example.demo.Repo.UserRepo;
import com.example.demo.Repo.VedioRepo;
import com.example.demo.Repo.VideoProgressRepo;
import com.example.demo.Response.CourseCompletionResponse;
import com.example.demo.Response.CourseProgressResponse;
import com.example.demo.Response.CourseTimeCompletionResponse;
import com.example.demo.Response.CourseVideoProgressItem;

@Service
public class VideoProgressService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private VedioRepo vedioRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private VideoProgressRepo videoProgressRepo;

    public VideoProgress saveOrUpdateProgress(String email, String videoId, Double currentTime, Double duration,
            Double watchedDeltaSeconds, Boolean completed) {
        if (email == null || videoId == null) {
            throw new RuntimeException("Email and videoId are required");
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User with email " + email + " not found");
        }

        Vedios video = vedioRepo.findById(videoId).orElse(null);
        if (video == null) {
            throw new RuntimeException("Video with id " + videoId + " not found");
        }

        Optional<VideoProgress> existingOpt = videoProgressRepo.findByUserAndVideo(user, video);
        VideoProgress progress = existingOpt.orElseGet(VideoProgress::new);

        progress.setUser(user);
        progress.setVideo(video);
        if (progress.getWatchedSeconds() == null) {
            progress.setWatchedSeconds(0.0);
        }

        progress.setLastPositionSeconds(currentTime != null ? currentTime : 0.0);
        if (duration != null) {
            progress.setDurationSeconds(duration);
        }

        if (watchedDeltaSeconds != null) {
            double delta = watchedDeltaSeconds.doubleValue();
            if (delta < 0) {
                delta = 0;
            }
            if (delta > 30) {
                delta = 30;
            }

            double newWatched = progress.getWatchedSeconds() + delta;
            Double dur = progress.getDurationSeconds();
            if (dur != null && dur > 0 && newWatched > dur) {
                newWatched = dur;
            }
            progress.setWatchedSeconds(newWatched);
        }

        if (completed != null) {
            progress.setCompleted(completed);
        } else if (progress.getCompleted() == null) {
            progress.setCompleted(false);
        }

        Double durAfterUpdate = progress.getDurationSeconds();
        Double timeAfterUpdate = progress.getLastPositionSeconds();
        if (durAfterUpdate != null && durAfterUpdate > 0 && timeAfterUpdate != null) {
            double thresholdSeconds = 1.0;
            if (timeAfterUpdate >= (durAfterUpdate - thresholdSeconds)) {
                progress.setCompleted(true);
            }
        }

        if (Boolean.TRUE.equals(progress.getCompleted())) {
            Double dur = progress.getDurationSeconds();
            if (dur != null && dur > 0) {
                progress.setWatchedSeconds(dur);
                progress.setLastPositionSeconds(dur);
            }
        }

        return videoProgressRepo.save(progress);
    }

    public VideoProgress getProgress(String email, String videoId) {
        if (email == null || videoId == null) {
            return null;
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            return null;
        }

        Vedios video = vedioRepo.findById(videoId).orElse(null);
        if (video == null) {
            return null;
        }

        return videoProgressRepo.findByUserAndVideo(user, video).orElse(null);
    }

    public CourseCompletionResponse getCourseCompletion(String email, String courseId) {
        if (email == null || courseId == null) {
            throw new RuntimeException("Email and courseId are required");
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User with email " + email + " not found");
        }

        Courses course = courseRepo.findById(courseId).orElse(null);
        if (course == null) {
            throw new RuntimeException("Course with id " + courseId + " not found");
        }

        int totalVideos = course.getVedios() == null ? 0 : course.getVedios().size();
        int completedVideos = (int) videoProgressRepo.countCompletedByUserAndCourseId(user, courseId);

        double percentage;
        if (totalVideos <= 0) {
            percentage = 0.0;
        } else {
            percentage = (completedVideos * 100.0) / totalVideos;
        }

        if (percentage > 100.0) {
            percentage = 100.0;
        }

        CourseCompletionResponse response = new CourseCompletionResponse();
        response.setEmail(email);
        response.setCourseId(courseId);
        response.setTotalVideos(totalVideos);
        response.setCompletedVideos(completedVideos);
        response.setCompletionPercentage(percentage);
        response.setCompleted(totalVideos > 0 && completedVideos >= totalVideos);
        return response;
    }

    public CourseTimeCompletionResponse getCourseTimeCompletion(String email, String courseId) {
        if (email == null || courseId == null) {
            throw new RuntimeException("Email and courseId are required");
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User with email " + email + " not found");
        }

        Courses course = courseRepo.findById(courseId).orElse(null);
        if (course == null) {
            throw new RuntimeException("Course with id " + courseId + " not found");
        }

        double totalDurationSeconds = videoProgressRepo.sumDurationSecondsByUserAndCourseId(user, courseId);
        double watchedSeconds = videoProgressRepo.sumWatchedSecondsByUserAndCourseId(user, courseId);

        if (watchedSeconds < 0) {
            watchedSeconds = 0.0;
        }

        if (totalDurationSeconds > 0 && watchedSeconds > totalDurationSeconds) {
            watchedSeconds = totalDurationSeconds;
        }

        double completionPercentage;
        if (totalDurationSeconds <= 0) {
            completionPercentage = 0.0;
        } else {
            completionPercentage = (watchedSeconds * 100.0) / totalDurationSeconds;
        }

        if (completionPercentage > 100.0) {
            completionPercentage = 100.0;
        }

        double remainingSeconds = Math.max(0.0, totalDurationSeconds - watchedSeconds);

        CourseTimeCompletionResponse response = new CourseTimeCompletionResponse();
        response.setEmail(email);
        response.setCourseId(courseId);
        response.setTotalDurationSeconds(totalDurationSeconds);
        response.setWatchedSeconds(watchedSeconds);
        response.setRemainingSeconds(remainingSeconds);
        response.setCompletionPercentage(completionPercentage);
        response.setCompleted(totalDurationSeconds > 0 && watchedSeconds >= totalDurationSeconds);
        return response;
    }

    public CourseProgressResponse getCourseProgress(String email, String courseId) {
        if (email == null || courseId == null) {
            throw new RuntimeException("Email and courseId are required");
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User with email " + email + " not found");
        }

        Courses course = courseRepo.findById(courseId).orElse(null);
        if (course == null) {
            throw new RuntimeException("Course with id " + courseId + " not found");
        }

        List<Object[]> rows = vedioRepo.findVideosWithProgressByUserAndCourseId(user, courseId);

        double totalDurationSeconds = 0.0;
        double watchedSeconds = 0.0;
        List<CourseVideoProgressItem> items = new ArrayList<>();

        for (Object[] row : rows) {
            Vedios v = (Vedios) row[0];
            VideoProgress vp = (VideoProgress) row[1];

            Double duration = vp != null ? vp.getDurationSeconds() : null;
            Double watched = vp != null ? vp.getWatchedSeconds() : null;

            double durSafe = (duration != null && duration > 0) ? duration.doubleValue() : 0.0;
            double watchedSafe = (watched != null && watched > 0) ? watched.doubleValue() : 0.0;

            if (durSafe > 0 && watchedSafe > durSafe) {
                watchedSafe = durSafe;
            }

            totalDurationSeconds += durSafe;
            watchedSeconds += watchedSafe;

            double videoPct = 0.0;
            if (durSafe > 0) {
                videoPct = (watchedSafe * 100.0) / durSafe;
            }
            if (videoPct > 100.0) {
                videoPct = 100.0;
            }

            CourseVideoProgressItem item = new CourseVideoProgressItem();
            item.setVideoId(v.getVedioId());
            item.setTitle(v.getTitle());
            item.setVideoUrl(v.getVideoUrl());
            item.setLastPositionSeconds(vp != null ? vp.getLastPositionSeconds() : 0.0);
            item.setDurationSeconds(durSafe);
            item.setWatchedSeconds(watchedSafe);
            item.setCompletionPercentage(videoPct);
            item.setCompleted(vp != null && Boolean.TRUE.equals(vp.getCompleted()));
            items.add(item);
        }

        if (watchedSeconds < 0) {
            watchedSeconds = 0.0;
        }

        if (totalDurationSeconds > 0 && watchedSeconds > totalDurationSeconds) {
            watchedSeconds = totalDurationSeconds;
        }

        double completionPercentage;
        if (totalDurationSeconds <= 0) {
            completionPercentage = 0.0;
        } else {
            completionPercentage = (watchedSeconds * 100.0) / totalDurationSeconds;
        }

        if (completionPercentage > 100.0) {
            completionPercentage = 100.0;
        }

        double remainingSeconds = Math.max(0.0, totalDurationSeconds - watchedSeconds);

        CourseProgressResponse response = new CourseProgressResponse();
        response.setEmail(email);
        response.setCourseId(courseId);
        response.setTotalDurationSeconds(totalDurationSeconds);
        response.setWatchedSeconds(watchedSeconds);
        response.setRemainingSeconds(remainingSeconds);
        response.setCompletionPercentage(completionPercentage);
        response.setCompleted(totalDurationSeconds > 0 && watchedSeconds >= totalDurationSeconds);
        response.setVideos(items);
        return response;
    }
}
