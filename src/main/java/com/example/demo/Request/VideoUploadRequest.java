package com.example.demo.Request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoUploadRequest {
    private String title;
    private String description;
    private MultipartFile video;
}