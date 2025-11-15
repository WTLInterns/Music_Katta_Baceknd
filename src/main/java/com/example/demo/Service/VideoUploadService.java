package com.example.demo.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class VideoUploadService {

    @Autowired
    private Cloudinary cloudinary;

    public Map uploadVideo(MultipartFile file) throws IOException {
        // Configure upload options for video compression
        Map uploadOptions = ObjectUtils.asMap(
                "resource_type", "video",
                "quality", "auto:good", // Good quality with compression
                "compress", true,  // Enable compression
                "format", "mp4",    // Ensure consistent format
                "video_codec", "h264", // Use efficient H.264 codec
                "audio_codec", "aac",   // Use efficient AAC audio codec
                "bit_rate", 5000000,    // Limit bitrate to 5Mbps
                "width", 1280,          // Resize to 720p width
                "height", 720,          // Resize to 720p height
                "crop", "limit"         // Maintain aspect ratio
        );

        return cloudinary.uploader().upload(file.getBytes(), uploadOptions);
    }

    public String getCurrentDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return formatter.format(date);
    }
    
    public String getCurrentTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
        Date date = new Date();
        return formatter.format(date);
    }
    
    public void deleteVideo(String publicId) throws IOException {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
        } catch (Exception e) {
            throw new IOException("Error deleting video from Cloudinary: " + e.getMessage());
        }
    }
}