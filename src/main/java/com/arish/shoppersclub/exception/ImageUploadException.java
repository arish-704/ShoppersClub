package com.arish.shoppersclub.exception;

/**
 * Exception thrown when Cloudinary SDK fails during an image upload operation.
 */
public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageUploadException(String message) {
        super(message);
    }
}
