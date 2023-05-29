package com.howtodoinjava.demo.publisher;

import com.howtodoinjava.demo.model.OrderEvents;
import com.howtodoinjava.demo.publisher.pubsub.service.RedisPubSubService;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
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
class OrderEventsPublisherApplicationTest {

    @Autowired
    private RedisPubSubService redisPubSubService;

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

        assertNotNull(redisPubSubService.publish(orderEvents));
    }

}