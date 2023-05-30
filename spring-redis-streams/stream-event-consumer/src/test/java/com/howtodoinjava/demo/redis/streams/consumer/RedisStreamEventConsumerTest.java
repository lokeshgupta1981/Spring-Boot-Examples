package com.howtodoinjava.demo.redis.streams.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.demo.redis.streams.consumer.service.PurchaseEventListener;
import com.howtodoinjava.demo.redis.streams.model.PurchaseEvent;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.DataInput;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class RedisStreamEventConsumerTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String streamKey = "purchase-events";

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

        ObjectRecord<String, PurchaseEvent> record = StreamRecords.newRecord()
                .ofObject(purchaseEvent)
                .withStreamKey(streamKey);

        RecordId recordId = this.redisTemplate.opsForStream()
                .add(record);

        Thread.sleep(2000);

        PurchaseEvent receivedEvent = objectMapper.readValue(
                redisTemplate.opsForValue().get(purchaseEvent.getPurchaseId()),
                PurchaseEvent.class);

        assertEquals(receivedEvent.getPurchaseId(), purchaseEvent.getPurchaseId());
        assertEquals(receivedEvent.getProductId(), purchaseEvent.getProductId());
        assertEquals(receivedEvent.getQuantity(), purchaseEvent.getQuantity());
        assertEquals(receivedEvent.getPrice(), purchaseEvent.getPrice());

        redisTemplate.expire(purchaseEvent.getPurchaseId(), Duration.ofSeconds(2));
    }

    private void createConsumerGroupIfNotExists(RedisConnectionFactory redisConnectionFactory, String streamKey, String groupName){
        try {
            try {
                redisConnectionFactory.getConnection().streamCommands()
                        .xGroupCreate(streamKey.getBytes(), streamKey, ReadOffset.from("0-0"), true);
            } catch (RedisSystemException exception) {

            }
        }
        catch (RedisSystemException ex){

        }
    }

}