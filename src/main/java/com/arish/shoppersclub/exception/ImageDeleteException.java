package com.arish.shoppersclub.exception;

/**
 * Exception thrown when Cloudinary SDK fails during an image deletion operation.
 */
public class ImageDeleteException extends RuntimeException {
    public ImageDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageDeleteException(String message) {
        super(message);
    }
}
