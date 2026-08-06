package com.arish.shoppersclub.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    void set(String key, Object value);
    void setWithTTL(String key, Object value, long timeout, TimeUnit timeUnit);
    Object get(String key);
    boolean delete(String key);
    boolean hasKey(String key);
    long getRemainingTTL(String key, TimeUnit timeUnit);
}
