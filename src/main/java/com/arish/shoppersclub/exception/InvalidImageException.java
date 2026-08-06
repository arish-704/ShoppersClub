package com.arish.shoppersclub.exception;

/**
 * Exception thrown when an uploaded image fails validation (empty, size > 5MB, or unsupported format).
 */
public class InvalidImageException extends RuntimeException {
    public InvalidImageException(String message) {
        super(message);
    }
}
