package com.howtodoinjava.demo.subscriber;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.demo.model.OrderEvent;
import com.howtodoinjava.demo.subscriber.pubsub.subscriber.OrderEventListener;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class MainTest {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @MockBean
  OrderEventListener orderEventListener;

  @Autowired
  private ObjectMapper objectMapper;

  @Container
  static RedisContainer REDIS_CONTAINER =
      new RedisContainer(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

  @DynamicPropertySource
  private static void registerRedisProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
  }

  @Test
  public void testOnMessage() throws Exception {

    OrderEvent orderEvent = OrderEvent.builder()
        .orderId("1")
        .userId("12")
        .productName("Mobile")
        .quantity(1)
        .price(42000)
        .build();

    redisTemplate.convertAndSend("order-events", orderEvent);

    Thread.sleep(1000);

    ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(Message.class);
    Mockito.verify(orderEventListener).onMessage(argumentCaptor.capture(), ArgumentMatchers.any());

    OrderEvent receivedEvent = objectMapper.readValue(argumentCaptor.getValue().getBody(),
        OrderEvent.class);

    assertEquals(receivedEvent.getOrderId(), orderEvent.getOrderId());
    assertEquals(receivedEvent.getQuantity(), orderEvent.getQuantity());
    assertEquals(receivedEvent.getPrice(), orderEvent.getPrice());
  }
}

