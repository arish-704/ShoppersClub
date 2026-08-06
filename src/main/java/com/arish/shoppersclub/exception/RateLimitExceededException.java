package com.arish.shoppersclub.exception;

/**
 * Exception thrown when a user exceeds the allowed API request rate limit.
 */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
