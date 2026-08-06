package com.arish.shoppersclub.security;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.arish.shoppersclub.annotation.RateLimit;
import com.arish.shoppersclub.exception.RateLimitExceededException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Spring MVC HandlerInterceptor that enforces Redis sliding window rate limits.
 *
 * How it works:
 * 1. Checks if the target controller method has the @RateLimit annotation.
 * 2. Extracts client IP address and endpoint URI to form Redis key: "rate_limit:<ip>:<uri>".
 * 3. Uses Redis INCR operation to atomically count incoming requests.
 * 4. Sets expiration TTL on the first request.
 * 5. Throws RateLimitExceededException if count exceeds the configured limit.
 */
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);

            if (rateLimit != null) {
                String clientIp = getClientIp(request);
                String uri = request.getRequestURI();
                String redisKey = "rate_limit:" + clientIp + ":" + uri;

                Long currentCount = redisTemplate.opsForValue().increment(redisKey);

                if (currentCount != null && currentCount == 1) {
                    redisTemplate.expire(redisKey, Duration.ofSeconds(rateLimit.periodSeconds()));
                }

                if (currentCount != null && currentCount > rateLimit.limit()) {
                    throw new RateLimitExceededException(
                            "Rate limit exceeded. Maximum " + rateLimit.limit()
                            + " requests allowed per " + rateLimit.periodSeconds() + " seconds."
                    );
                }
            }
        }
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
