package com.howtodoinjava.demo.redis.streams.producer;

import com.howtodoinjava.demo.redis.streams.model.PurchaseEvent;
import com.howtodoinjava.demo.redis.streams.producer.service.PurchaseStreamProducer;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class RedisStreamEventProducerTest {

  @Autowired
  private PurchaseStreamProducer eventProducer;

  @Container
  private static final RedisContainer REDIS_CONTAINER =
      new RedisContainer(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

  @DynamicPropertySource
  private static void registerRedisProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    registry.add("spring.data.redis.port", () -> REDIS_CONTAINER
        .getMappedPort(6379).toString());
  }

  @Test
  public void testProduce() throws Exception {

    PurchaseEvent purchaseEvent = PurchaseEvent.builder()
        .purchaseId("1")
        .productId("12")
        .productName("IPhone 14")
        .quantity(1)
        .price(74000)
        .build();

    assertNotNull(eventProducer.produce(purchaseEvent));
  }
}