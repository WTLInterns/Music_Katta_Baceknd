# Video Upload API Documentation

## Overview
This API allows uploading videos to Cloudinary with automatic compression while maintaining quality. Videos are associated with courses in the system.

## Endpoints

### Upload Video
```
POST /api/videos/upload/{courseId}
```

#### Parameters
- `courseId` (path parameter): The ID of the course to associate the video with
- `video` (form parameter): The video file to upload (multipart/form-data)
- `title` (form parameter): The title of the video
- `description` (form parameter): The description of the video

#### Response
```json
{
  "videoId": "string",
  "title": "string",
  "description": "string",
  "videoUrl": "string",
  "postDate": "string",
  "courseId": "string"
}
```

### Create Course
```
POST /course/create-course
```

#### Request Body
```json
{
  "courseName": "string",
  "details": "string",
  "price": "string"
}
```

#### Response
Returns the created course object with a generated courseId and postDate in dd-MM-yyyy format.

## Video Processing
Videos are automatically:
- Compressed to maintain quality while reducing file size
- Resized to 720p (1280x720) while maintaining aspect ratio
- Converted to MP4 format with H.264 video codec and AAC audio codec
- Limited to 5Mbps bitrate for optimal web streaming

## Cloudinary Configuration
The application uses Cloudinary for video storage and processing. Credentials are configured in `application.properties`.