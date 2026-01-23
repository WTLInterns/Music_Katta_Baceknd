# Course Image API Documentation

This document explains how to use the new course image functionality that has been added to the Music Katta backend.

## New Endpoints

### 1. Create Course with Image
**POST** `/course/create-course-with-image`

Create a new course with an optional image upload to Cloudinary.

#### Request Parameters (Form Data)
- `courseName` (String, required): Name of the course
- `details` (String, required): Description of the course
- `price` (String, required): Price of the course
- `image` (MultipartFile, optional): Course image file (JPG, PNG, etc.)

#### Response
Returns the created course object with Cloudinary image URL if an image was uploaded.

#### Example using curl:
```bash
curl -X POST "http://localhost:8085/course/create-course-with-image" \
  -F "courseName=Music Theory Basics" \
  -F "details=Learn the fundamentals of music theory" \
  -F "price=499" \
  -F "image=@/path/to/image.jpg"
```

### 2. Update Course with Image
**PUT** `/course/update-course/{courseId}`

Update an existing course and optionally update its image.

#### Path Parameters
- `courseId` (String, required): ID of the course to update

#### Request Parameters (Form Data)
- `courseName` (String, required): Updated name of the course
- `details` (String, required): Updated description of the course
- `price` (String, required): Updated price of the course
- `image` (MultipartFile, optional): New course image file (JPG, PNG, etc.)

#### Response
Returns the updated course object with Cloudinary image URL if an image was uploaded.

#### Example using curl:
```bash
curl -X PUT "http://localhost:8085/course/update-course/123e4567-e89b-12d3-a456-426614174000" \
  -F "courseName=Advanced Music Theory" \
  -F "details=Advanced concepts in music theory" \
  -F "price=799" \
  -F "image=@/path/to/new-image.jpg"
```

### 3. Delete Course
**DELETE** `/course/delete-course/{courseId}`

Delete a course and its associated image from Cloudinary.

#### Path Parameters
- `courseId` (String, required): ID of the course to delete

#### Response
Returns a success message if the course was deleted.

#### Example using curl:
```bash
curl -X DELETE "http://localhost:8085/course/delete-course/123e4567-e89b-12d3-a456-426614174000"
```

## Database Changes

The `Courses` entity has been updated with two new fields:
- `courseImageUrl` (String): URL of the course image stored in Cloudinary
- `courseImagePublicId` (String): Public ID of the image in Cloudinary (used for deletion)

## Cloudinary Configuration

The application is configured to use Cloudinary for image storage. The configuration is set in `application.properties`:
- `cloudinary.cloud-name`: Your Cloudinary cloud name
- `cloudinary.api-key`: Your Cloudinary API key
- `cloudinary.api-secret`: Your Cloudinary API secret

## Frontend Integration

To integrate this functionality in your frontend:

1. When creating a course, use the `/course/create-course-with-image` endpoint
2. When updating a course, use the `/course/update-course/{courseId}` endpoint
3. Display the `courseImageUrl` in your course listings and details pages
4. Handle image uploads as multipart form data

## Example Response

```json
{
  "courseId": "123e4567-e89b-12d3-a456-426614174000",
  "courseName": "Music Theory Basics",
  "details": "Learn the fundamentals of music theory",
  "postDate": "18-11-2025",
  "postTime": "02:30 PM",
  "price": "499",
  "courseImageUrl": "https://res.cloudinary.com/your-cloud-name/image/upload/v1234567890/course-images/abc123.jpg"
}
```

## Error Handling

All endpoints return appropriate HTTP status codes:
- 200: Success
- 404: Course not found
- 500: Server error (with error message)