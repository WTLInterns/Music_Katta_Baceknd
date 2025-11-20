package com.example.demo.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.User;
import com.example.demo.Entity.Vedios;
import com.example.demo.Entity.VideoProgress;
import com.example.demo.Repo.UserRepo;
import com.example.demo.Repo.VedioRepo;
import com.example.demo.Repo.VideoProgressRepo;

@Service
public class VideoProgressService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private VedioRepo vedioRepo;

    @Autowired
    private VideoProgressRepo videoProgressRepo;

    public VideoProgress saveOrUpdateProgress(String email, String videoId, Double currentTime, Double duration,
            Boolean completed) {
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
        progress.setLastPositionSeconds(currentTime != null ? currentTime : 0.0);
        if (duration != null) {
            progress.setDurationSeconds(duration);
        }
        if (completed != null) {
            progress.setCompleted(completed);
        } else if (progress.getCompleted() == null) {
            progress.setCompleted(false);
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
}
