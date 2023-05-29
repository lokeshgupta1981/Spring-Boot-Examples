package com.howtodoinjava.demo.subscriber.pubsub.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.demo.model.OrderEvents;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

@Slf4j
@Configuration
public class PubSubMessageListener implements MessageListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("pubsubRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            log.info("New message received: {}", message);
            OrderEvents orderEvents = objectMapper.readValue(message.getBody(), OrderEvents.class);
            redisTemplate.opsForValue().set(orderEvents.getOrderId(), orderEvents);
        } catch (IOException e) {
            log.error("error while parsing message");
        }

    }
}
