package com.howtodoinjava.demo;


import com.howtodoinjava.demo.service.RedisCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SpringDataRedisLettuceApplicationTests {

  @Autowired
  private RedisCacheService cacheUtil;

  String key;
  String value;
  String hashKey;

  @BeforeEach
  public void setUp() {
    key = "Name";
    value = "Java";
    hashKey = "Subject";
  }

  @Test
  public void testValueOps() {

    cacheUtil.putSimple(key, value);
    String retrievedValue = cacheUtil.getSimple(key);
    assertEquals(value, retrievedValue);
  }

  @Test
  public void testHashOps() {
    cacheUtil.put(hashKey, key, value, 60L);
    String fetchedValue = cacheUtil.get(hashKey, key);
    assertEquals(value, fetchedValue);
  }
}
