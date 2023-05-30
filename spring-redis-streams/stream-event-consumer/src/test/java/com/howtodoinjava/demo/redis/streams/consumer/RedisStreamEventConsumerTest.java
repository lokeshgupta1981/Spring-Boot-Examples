package com.howtodoinjava.demo.redis.streams.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.demo.redis.streams.model.PurchaseEvent;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.DataInput;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class RedisStreamEventConsumerTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean(name = "purchaseStreamListener")
    StreamListener<String, ObjectRecord<String, PurchaseEvent>> purchaseStreamListener;

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

        PurchaseEvent purchaseEvent = PurchaseEvent.builder()
                .purchaseId("1")
                .productId("12")
                .productName("IPhone 14")
                .quantity(1)
                .price(74000)
                .build();

        String streamKey = "purchase-events";
        ObjectRecord<String, PurchaseEvent> record = StreamRecords.newRecord()
                .ofObject(purchaseEvent)
                .withStreamKey(streamKey);

        this.redisTemplate.opsForStream()
                .add(record);


        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        verify(purchaseStreamListener, times(1))
                .onMessage(isA(ObjectRecord.class));

        PurchaseEvent receivedEvent = objectMapper.readValue(
                redisTemplate.opsForValue().get(purchaseEvent.getPurchaseId()),
                PurchaseEvent.class);

        assertEquals(receivedEvent.getPurchaseId(), purchaseEvent.getPurchaseId());
        assertEquals(receivedEvent.getProductId(), purchaseEvent.getProductId());
        assertEquals(receivedEvent.getQuantity(), purchaseEvent.getQuantity());
        assertEquals(receivedEvent.getPrice(), purchaseEvent.getPrice());

        redisTemplate.expire(purchaseEvent.getPurchaseId(), Duration.ofSeconds(2));
    }

}