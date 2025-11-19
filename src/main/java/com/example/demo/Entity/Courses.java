package com.example.demo.Entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Courses {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String courseId;

    private String courseName;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String details;

    private String postDate;

    private String postTime;

    private String price; // This will be the discounted price
    
    private String originalPrice; // Original price before discount
    
    private String discountPercentage; // Discount percentage
    
    private String status; // ACTIVE or INACTIVE

    // Course image URL from Cloudinary
    private String courseImageUrl;
    
    // Cloudinary public ID for the course image
    private String courseImagePublicId;

    private List<String> keywords;


private String courseDuration;


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vedios> vedios;

}