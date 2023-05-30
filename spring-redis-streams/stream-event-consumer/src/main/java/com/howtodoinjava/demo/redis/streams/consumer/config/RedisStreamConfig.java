package com.howtodoinjava.demo.redis.streams.consumer.config;

import com.howtodoinjava.demo.redis.streams.consumer.service.PurchaseStreamListener;
import com.howtodoinjava.demo.redis.streams.model.PurchaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisStreamConfig {

  @Value("${stream.key:purchase-events}")
  private String streamKey;

  @Bean
  public StreamListener<String, ObjectRecord<String, PurchaseEvent>> purchaseStreamListener() {
    // handle message from stream
    return new PurchaseStreamListener();
  }

  @Bean
  public Subscription subscription(RedisConnectionFactory connectionFactory)
      throws UnknownHostException {

    createConsumerGroupIfNotExists(connectionFactory, streamKey, streamKey);
    StreamOffset<String> streamOffset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());

    StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String,
        ObjectRecord<String, PurchaseEvent>> options = StreamMessageListenerContainer
        .StreamMessageListenerContainerOptions
        .builder()
        .pollTimeout(Duration.ofMillis(100))
        .targetType(PurchaseEvent.class)
        .build();

    StreamMessageListenerContainer<String, ObjectRecord<String, PurchaseEvent>> container =
        StreamMessageListenerContainer
            .create(connectionFactory, options);

    Subscription subscription =
        container.receiveAutoAck(Consumer.from(streamKey, InetAddress.getLocalHost().getHostName()),
            streamOffset, purchaseStreamListener());

    /**
     * container.receiveAutoAck(....)
     * can also be used to auto acknowledge the messages just after being received.
     */

    container.start();
    return subscription;
  }

  private void createConsumerGroupIfNotExists(RedisConnectionFactory redisConnectionFactory,
      String streamKey, String groupName) {
    try {
      try {
        redisConnectionFactory.getConnection().streamCommands()
            .xGroupCreate(streamKey.getBytes(), streamKey, ReadOffset.from("0-0"), true);
      } catch (RedisSystemException exception) {
        log.warn(exception.getCause().getMessage());
      }
    } catch (RedisSystemException ex) {
      log.error(ex.getMessage());
    }
  }
}