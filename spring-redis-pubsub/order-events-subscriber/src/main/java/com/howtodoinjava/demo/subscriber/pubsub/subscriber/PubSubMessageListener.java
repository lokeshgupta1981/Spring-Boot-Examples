package com.howtodoinjava.demo.subscriber.pubsub.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.demo.model.OrderEvents;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.io.IOException;

@Slf4j
@Configuration
@AllArgsConstructor
public class PubSubMessageListener implements MessageListener {

    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            OrderEvents orderEvents = objectMapper.readValue(message.getBody(), OrderEvents.class);
            log.info("New message received Sync: {}", orderEvents);
        } catch (IOException e) {
            log.error("error while parsing message");
        }

    }
}
