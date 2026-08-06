package com.arish.shoppersclub.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Storage Abstraction Service interface for decoupling cloud media providers (Cloudinary, AWS S3, Firebase)
 * from business services.
 */
public interface ImageStorageService {

    /**
     * Uploads a MultipartFile to the cloud storage provider and returns the hosted secure URL.
     *
     * @param file Uploaded image file
     * @return Secure HTTPS URL of the hosted image
     */
    String uploadImage(MultipartFile file);

    /**
     * Deletes an image from cloud storage given its hosted URL.
     *
     * @param imageUrl Hosted secure image URL
     */
    void deleteImage(String imageUrl);

}
