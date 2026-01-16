package com.example.demo.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.User;
import com.example.demo.Entity.Vedios;

@Repository
public interface VedioRepo extends JpaRepository<Vedios, String> {

    @Query("select v, vp from Vedios v left join VideoProgress vp on vp.video = v and vp.user = :user where v.course.courseId = :courseId")
    List<Object[]> findVideosWithProgressByUserAndCourseId(@Param("user") User user, @Param("courseId") String courseId);

}
