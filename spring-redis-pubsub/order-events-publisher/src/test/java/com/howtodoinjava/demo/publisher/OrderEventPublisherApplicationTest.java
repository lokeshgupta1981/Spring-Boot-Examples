package com.howtodoinjava.demo.publisher;

import com.howtodoinjava.demo.model.OrderEvent;
import com.howtodoinjava.demo.publisher.pubsub.service.OrderService;
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
class OrderEventPublisherApplicationTest {

    @Autowired
    private OrderService orderService;

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

    @Test
    public void testOnMessage() throws Exception {

        OrderEvent orderEvent = OrderEvent.builder()
                .orderId("1")
                .userId("12")
                .productName("Mobile")
                .quantity(1)
                .price(42000)
                .build();

        assertNotNull(orderService.publish(orderEvent));
    }

}