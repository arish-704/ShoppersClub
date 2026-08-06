package com.arish.shoppersclub.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to enforce API rate limiting on specific controller endpoints using Redis.
 *
 * Example: @RateLimit(limit = 5, periodSeconds = 60) restricts requests to max 5 per minute per IP.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * Maximum number of allowed requests within the time window.
     */
    int limit() default 5;

    /**
     * Time window duration in seconds.
     */
    int periodSeconds() default 60;

}
