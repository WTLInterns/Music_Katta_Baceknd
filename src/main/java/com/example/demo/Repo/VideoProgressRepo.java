package com.example.demo.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.VideoProgress;
import com.example.demo.Entity.User;
import com.example.demo.Entity.Vedios;

@Repository
public interface VideoProgressRepo extends JpaRepository<VideoProgress, String> {

    Optional<VideoProgress> findByUserAndVideo(User user, Vedios video);
}
