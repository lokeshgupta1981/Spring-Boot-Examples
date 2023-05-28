package com.howtodoinjava.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class RedisCacheService {

  private RedisTemplate<Object, Object> redisTemplate;

  @Autowired
  public RedisCacheService(
      @Qualifier("customRedisTemplate") RedisTemplate<Object, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public <T> T getSimple(Object key) {
    return (T) redisTemplate.opsForValue().get(key);
  }

  public void putSimple(Object key, Object value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public void putSimpleWithTTL(Object key, Object value, Long ttl) {
    redisTemplate.opsForValue().set(key, value);
    redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
  }

  public <T> T get(Object hashKey, Object key) {
    return (T) redisTemplate.opsForHash().get(hashKey, key);
  }

  public void put(Object hashKey, Object key, Object value, Long ttl) {
    redisTemplate.opsForHash().put(hashKey, key, value);
    redisTemplate.expire(hashKey, Duration.ofSeconds(ttl));
  }

  public Long delete(Object hashKey, Object key) {
    return redisTemplate.opsForHash().delete(hashKey, key);
  }

  public void putWithTTL(Object hashKey, Object key, Object value, long ttl) {
    redisTemplate.opsForHash().put(hashKey, key, value);
    redisTemplate.expire(hashKey, Duration.ofSeconds(ttl));
  }

  public void expire(Object hashKey, long ttl) {
    redisTemplate.expire(hashKey, Duration.ofSeconds(ttl));
  }
}
