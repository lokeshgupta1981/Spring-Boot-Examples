package com.howtodoinjava.demo;


import com.howtodoinjava.demo.service.RedisCacheService;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class SpringDataRedisLettuceApplicationTests {

  @Autowired
  private RedisCacheService cacheUtil;

  @Container
  private static final RedisContainer REDIS_CONTAINER =
          new RedisContainer(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

  String key;
  String value;
  String hashKey;

  @DynamicPropertySource
  private static void registerRedisProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379)
            .toString());
    registry.add("default.redis.connection", () -> "standalone");
  }

  @BeforeEach
  public void setUp() {
    key = "Name";
    value = "Java";
    hashKey = "Subject";
  }

  @Test
  void givenRedisContainerConfiguredWithDynamicProperties_whenCheckingRunningStatus_thenStatusIsRunning() {
    assertTrue(REDIS_CONTAINER.isRunning());
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
