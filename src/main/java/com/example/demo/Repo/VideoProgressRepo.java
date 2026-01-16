package com.example.demo.Repo;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.VideoProgress;
import com.example.demo.Entity.User;
import com.example.demo.Entity.Vedios;

@Repository
public interface VideoProgressRepo extends JpaRepository<VideoProgress, String> {

    Optional<VideoProgress> findByUserAndVideo(User user, Vedios video);

    @Query("select vp from VideoProgress vp where vp.user = :user and vp.video.course.courseId = :courseId")
    List<VideoProgress> findByUserAndCourseId(@Param("user") User user, @Param("courseId") String courseId);

    @Query("select count(vp) from VideoProgress vp where vp.user = :user and vp.video.course.courseId = :courseId and vp.completed = true")
    long countCompletedByUserAndCourseId(@Param("user") User user, @Param("courseId") String courseId);

    @Query("select coalesce(sum(vp.watchedSeconds), 0) from VideoProgress vp where vp.user = :user and vp.video.course.courseId = :courseId")
    double sumWatchedSecondsByUserAndCourseId(@Param("user") User user, @Param("courseId") String courseId);

    @Query("select coalesce(sum(vp.durationSeconds), 0) from VideoProgress vp where vp.user = :user and vp.video.course.courseId = :courseId")
    double sumDurationSecondsByUserAndCourseId(@Param("user") User user, @Param("courseId") String courseId);
}
