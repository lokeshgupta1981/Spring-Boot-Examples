package com.howtodoinjava.demo;

import com.howtodoinjava.demo.service.RedisCacheService;
import com.redis.lettucemod.output.SampleOutput;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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
  private RedisCacheService redisCacheService;

  @Container
  /*@ServiceConnection*/
  private static final RedisContainer REDIS_CONTAINER =
      new RedisContainer(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

  String key;
  String value;
  String hashKey;

  @DynamicPropertySource
  private static void registerRedisProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    registry.add("default.redis.connection", () -> "standalone");
  }

  @BeforeEach
  public void setUp() {
    key = "Name";
    value = "Java";
    hashKey = "Subject";
  }

  @Test
  void testSetup() {
    System.out.println(REDIS_CONTAINER.getFirstMappedPort());
    assertTrue(REDIS_CONTAINER.isRunning());
  }

  @Test
  public void testValueOps() {

    redisCacheService.putSimple(key, value);
    String retrievedValue = redisCacheService.getSimple(key);
    assertEquals(value, retrievedValue);
  }

  @Test
  public void testHashOps() {
    redisCacheService.put(hashKey, key, value, 60L);
    String fetchedValue = redisCacheService.get(hashKey, key);
    assertEquals(value, fetchedValue);
  }
}
