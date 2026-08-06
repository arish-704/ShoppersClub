package com.arish.shoppersclub.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.service.RedisService;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for performing direct Redis operations using RedisTemplate.
 */
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Stores a key-value pair in Redis with no expiration time.
     *
     * @param key   The cache key identifier.
     * @param value The data object to store in Redis.
     */
    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Stores a key-value pair in Redis with a specified Time-To-Live (TTL).
     *
     * @param key      The cache key identifier.
     * @param value    The data object to store in Redis.
     * @param timeout  The duration before the key automatically expires.
     * @param timeUnit The unit of time for the timeout (e.g., SECONDS, MINUTES).
     */
    @Override
    public void setWithTTL(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * Retrieves the stored value associated with the given key from Redis.
     *
     * @param key The cache key identifier.
     * @return The cached object, or null if the key does not exist or has expired.
     */
    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Deletes a key and its associated value from Redis.
     *
     * @param key The cache key identifier to remove.
     * @return true if the key existed and was deleted; false otherwise.
     */
    @Override
    public boolean delete(String key) {
        Boolean result = redisTemplate.delete(key);
        return Boolean.TRUE.equals(result);
    }

    /**
     * Checks whether a specific key exists in Redis.
     *
     * @param key The cache key identifier to check.
     * @return true if the key exists and has not expired; false otherwise.
     */
    @Override
    public boolean hasKey(String key) {
        Boolean result = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(result);
    }

    /**
     * Retrieves the remaining lifespan (TTL) of a key in the specified time unit.
     *
     * @param key      The cache key identifier.
     * @param timeUnit The desired unit of time for the result.
     * @return The remaining expiration time, or -1 if the key has no TTL / does not exist.
     */
    @Override
    public long getRemainingTTL(String key, TimeUnit timeUnit) {
        Long expire = redisTemplate.getExpire(key, timeUnit);
        return expire != null ? expire : -1;
    }
}