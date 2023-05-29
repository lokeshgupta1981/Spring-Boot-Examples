package com.howtodoinjava.demo.subscriber;

import com.howtodoinjava.demo.model.OrderEvents;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class MainTest {

    @Autowired
    @Qualifier("pubsubRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Container
    /*@ServiceConnection*/
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER
                .getMappedPort(6379).toString());
    }

    @AfterEach
    public void destroy() {
        REDIS_CONTAINER.stop();
        REDIS_CONTAINER.close();
    }

    @Test
    public void testOnMessage() throws Exception {
        OrderEvents orderEvents = OrderEvents.builder()
                .orderId("1")
                .userId("12")
                .productName("Mobile")
                .quantity(1)
                .price(42000)
                .build();

        redisTemplate.convertAndSend("order-events", orderEvents);

        Thread.sleep(1000);

        OrderEvents processedData = (OrderEvents) redisTemplate.opsForValue()
                .get(orderEvents.getOrderId());

        assertEquals(processedData.getOrderId(), orderEvents.getOrderId());
        assertEquals(processedData.getQuantity(), orderEvents.getQuantity());
        assertEquals(processedData.getPrice(), orderEvents.getPrice());

        redisTemplate.expire(orderEvents.getOrderId(),  Duration.ofSeconds(2));
    }
}