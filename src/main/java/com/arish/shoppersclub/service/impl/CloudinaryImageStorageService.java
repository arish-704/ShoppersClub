package com.arish.shoppersclub.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.arish.shoppersclub.exception.ImageDeleteException;
import com.arish.shoppersclub.exception.ImageUploadException;
import com.arish.shoppersclub.exception.InvalidImageException;
import com.arish.shoppersclub.service.ImageStorageService;

import lombok.RequiredArgsConstructor;

/**
 * Cloudinary Implementation of ImageStorageService for uploading and deleting product images.
 */
@Service
@RequiredArgsConstructor
public class CloudinaryImageStorageService implements ImageStorageService {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    @Override
    public String uploadImage(MultipartFile file) {
        validateFile(file);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "shoppersclub/products")
            );

            String secureUrl = (String) uploadResult.get("secure_url");
            if (secureUrl == null || secureUrl.isBlank()) {
                throw new ImageUploadException("Cloudinary upload failed: secure_url was not returned");
            }

            return secureUrl;

        } catch (IOException ex) {
            throw new ImageUploadException("Failed to upload image file to Cloudinary", ex);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId != null && !publicId.isBlank()) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception ex) {
            throw new ImageDeleteException("Failed to delete image from Cloudinary for URL: " + imageUrl, ex);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("Uploaded image file cannot be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidImageException("Unsupported image format. Allowed formats: JPEG, PNG, WEBP");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidImageException("Image file size exceeds the maximum limit of 5 MB");
        }
    }

    /**
     * Helper method to extract the Cloudinary public ID (e.g., "shoppersclub/products/abc123") from a full URL.
     * URL Format: https://res.cloudinary.com/<cloud>/image/upload/v1234567/shoppersclub/products/abc123.jpg
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1) {
                return null;
            }
            String pathAfterUpload = imageUrl.substring(uploadIndex + 8); // Skip "/upload/"

            // Remove version prefix if present (e.g., "v1234567/")
            if (pathAfterUpload.startsWith("v")) {
                int slashIndex = pathAfterUpload.indexOf("/");
                if (slashIndex != -1) {
                    pathAfterUpload = pathAfterUpload.substring(slashIndex + 1);
                }
            }

            // Remove file extension (e.g., ".jpg")
            int dotIndex = pathAfterUpload.lastIndexOf(".");
            if (dotIndex != -1) {
                pathAfterUpload = pathAfterUpload.substring(0, dotIndex);
            }

            return pathAfterUpload;
        } catch (Exception ex) {
            return null;
        }
    }
}
