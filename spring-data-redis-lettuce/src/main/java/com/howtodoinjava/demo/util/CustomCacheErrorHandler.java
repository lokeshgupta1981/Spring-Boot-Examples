package com.howtodoinjava.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Slf4j
public class CustomCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.info("Failure getting from cache: " + cache.getName() + ", exception: " + exception.toString());
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.info("Failure putting into cache: " + cache.getName() + ", exception: " + exception.toString());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.info("Failure evicting from cache: " + cache.getName() + ", exception: " + exception.toString());
     }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.info("Failure clearing cache: " + cache.getName() + ", exception: " + exception.toString());
    }
}