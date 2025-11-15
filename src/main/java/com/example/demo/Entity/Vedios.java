package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vedios {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String vedioId;

    private String vedioDescription;

    private String title;

    private String postDate;

    private String postTime;

    private String videoUrl;

    private String cloudinaryPublicId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Courses course;

}
